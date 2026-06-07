package com.kebab.v2rayk.wrapper.config.import

import com.kebab.v2rayk.wrapper.config.Outbound
import com.kebab.v2rayk.wrapper.config.V2rayProperties
import com.kebab.v2rayk.wrapper.config.bound.ProtocolType
import com.kebab.v2rayk.wrapper.config.bound.settings.ShadowsocksOutboundSettings
import com.kebab.v2rayk.wrapper.config.bound.settings.ShadowsocksServer
import java.util.Base64

/**
 * Shadowsocks 分享链接解析策略（SIP002 格式）。
 *
 * 格式: ss://<base64(method:password)>@<host>:<port>#<tag>
 */
class ShadowsocksStrategy : ImportStrategy {
    override val supportedSchemes: List<String> = listOf("ss")

    override fun parse(url: String): V2rayProperties {
        val content = url.removePrefix("ss://").trim()

        val parsed = try {
            parseSip002(content)
        } catch (e: ImportException) {
            throw e
        } catch (e: Exception) {
            throw InvalidUrlFormatException("Shadowsocks URL 解析失败: ${e.message}", e)
        }

        val nodeName = parsed.nodeName ?: "${parsed.host}:${parsed.port}"

        val outbound = Outbound(
            protocol = ProtocolType.SHADOWSOCKS,
            sendThrough = "0.0.0.0",
            tag = nodeName,
            settings = ShadowsocksOutboundSettings(
                servers = listOf(
                    ShadowsocksServer(
                        address = parsed.host,
                        port = parsed.port,
                        method = parsed.method,
                        password = parsed.password,
                    )
                ),
            ),
        )

        return V2rayProperties(outbounds = listOf(outbound))
    }

    /**
     * 解析 SIP002 格式: <base64(method:password)>@<host>:<port>#<tag>
     */
    private fun parseSip002(content: String): SSParsed {
        // Extract fragment (#tag) first
        val fragmentIndex = content.indexOf('#')
        val bodyWithoutFragment: String
        val nodeName: String?
        if (fragmentIndex >= 0) {
            bodyWithoutFragment = content.substring(0, fragmentIndex)
            val rawFragment = content.substring(fragmentIndex + 1)
            // If fragment contains @, use the first part as the display name
            nodeName = rawFragment.substringBefore("@").ifBlank { rawFragment }
        } else {
            bodyWithoutFragment = content
            nodeName = null
        }

        val atIndex = bodyWithoutFragment.indexOf('@')
        if (atIndex == -1) throw InvalidUrlFormatException("SS URL 缺少 @ 分隔符")

        // Base64 decode method:password
        val encodedUserInfo = bodyWithoutFragment.substring(0, atIndex)
        val decodedUserInfo = try {
            String(Base64.getDecoder().decode(encodedUserInfo))
        } catch (e: Exception) {
            throw InvalidUrlFormatException("Base64 解码失败: ${e.message}", e)
        }

        val colonIndex = decodedUserInfo.indexOf(':')
        if (colonIndex == -1) throw InvalidUrlFormatException("SS 用户信息格式错误，缺少 method:password 分隔符")
        val method = decodedUserInfo.substring(0, colonIndex)
        val password = decodedUserInfo.substring(colonIndex + 1)

        // Parse host:port
        val hostPort = bodyWithoutFragment.substring(atIndex + 1)
        val lastColonIndex = hostPort.lastIndexOf(':')
        val host: String
        val port: Int
        if (lastColonIndex >= 0 && hostPort.substring(lastColonIndex + 1).all { it.isDigit() }) {
            host = hostPort.substring(0, lastColonIndex)
            port = hostPort.substring(lastColonIndex + 1).toIntOrNull()
                ?: throw InvalidUrlFormatException("非法端口: ${hostPort.substring(lastColonIndex + 1)}")
        } else {
            host = hostPort
            port = 8388 // SS default port
        }

        if (port !in 1..65535) throw InvalidUrlFormatException("端口超出范围: $port")

        return SSParsed(
            host = host,
            port = port,
            method = method,
            password = password,
            nodeName = nodeName,
        )
    }

    private data class SSParsed(
        val host: String,
        val port: Int,
        val method: String,
        val password: String,
        val nodeName: String?,
    )
}
