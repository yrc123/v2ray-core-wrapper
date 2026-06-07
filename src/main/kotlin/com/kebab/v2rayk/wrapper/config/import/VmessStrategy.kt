package com.kebab.v2rayk.wrapper.config.import

import com.kebab.v2rayk.wrapper.config.Outbound
import com.kebab.v2rayk.wrapper.config.V2rayProperties
import com.kebab.v2rayk.wrapper.config.bound.ProtocolType
import com.kebab.v2rayk.wrapper.config.bound.settings.VmessOutboundSettings
import com.kebab.v2rayk.wrapper.config.bound.settings.VmessServer
import com.kebab.v2rayk.wrapper.config.bound.settings.VmessUser
import com.kebab.v2rayk.wrapper.config.bound.settings.VmessSecurity
import com.kebab.v2rayk.wrapper.config.bound.stream.StreamSettings
import com.kebab.v2rayk.wrapper.config.bound.stream.TransportNetwork
import com.kebab.v2rayk.wrapper.config.bound.stream.TlsSettings
import java.util.Base64

/**
 * VMess 分享链接解析策略。
 *
 * 支持两种格式：
 * 1. V2RayN 明文格式: vmess://[{network}:]{uuid}-{aid}@{host}:{port}/{path}#{tag}
 * 2. 标准 Base64 格式: vmess://<base64-encoded JSON>
 */
class VmessStrategy : ImportStrategy {
    override val supportedSchemes: List<String> = listOf("vmess")

    override fun parse(url: String): V2rayProperties {
        val content = url.removePrefix("vmess://").trim()

        val parsed: VmessParsed = try {
            parseContent(content)
        } catch (e: ImportException) {
            throw e
        } catch (e: Exception) {
            throw InvalidUrlFormatException("VMess URL 解析失败: ${e.message}", e)
        }

        val nodeName = parsed.nodeName ?: "${parsed.host}:${parsed.port}"

        val outbound = Outbound(
            protocol = ProtocolType.VMESS,
            sendThrough = "0.0.0.0",
            tag = nodeName,
            settings = VmessOutboundSettings(
                vnext = listOf(
                    VmessServer(
                        address = parsed.host,
                        port = parsed.port,
                        users = listOf(
                            VmessUser(
                                id = parsed.uuid,
                                alterId = parsed.aid,
                                security = VmessSecurity.AES_128_GCM,
                            )
                        ),
                    )
                ),
            ),
            streamSettings = StreamSettings(
                network = TransportNetwork.TCP,
                tlsSettings = TlsSettings(disableSystemRoot = false),
            ),
        )

        return V2rayProperties(outbounds = listOf(outbound))
    }

    private fun parseContent(content: String): VmessParsed {
        // Try Base64-encoded JSON format first (only if content looks like base64)
        if (!content.contains("@") && !content.contains("#") && !content.contains("?")) {
            try {
                val json = String(Base64.getDecoder().decode(content))
                val mapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
                val node = mapper.readTree(json)
                return VmessParsed(
                    host = node["add"]?.asText()
                        ?: throw InvalidUrlFormatException("缺少 add 字段"),
                    port = node["port"]?.asText()?.toIntOrNull()
                        ?: throw InvalidUrlFormatException("缺少或非法 port 字段"),
                    uuid = node["id"]?.asText()
                        ?: throw InvalidUrlFormatException("缺少 id 字段"),
                    aid = node["aid"]?.asText()?.toIntOrNull() ?: 0,
                    nodeName = node["ps"]?.asText(),
                )
            } catch (_: InvalidUrlFormatException) {
                // Fall through to V2RayN format parsing
            } catch (_: Exception) {
                // Fall through to V2RayN format parsing
            }
        }

        // V2RayN 明文格式: [{network}:]{uuid}-{aid}@{host}:{port}/{path}#{fragment}
        return parseV2RayNFormat(content)
    }

    private fun parseV2RayNFormat(content: String): VmessParsed {
        var remaining = content

        // Extract optional network prefix (e.g., "tcp:", "ws:")
        val knownNetworks = setOf("tcp", "ws", "kcp", "quic", "grpc", "http")
        if (":" in remaining && remaining.substringBefore(":") in knownNetworks) {
            remaining = remaining.substringAfter(":")
        }

        // Parse uuid-aid@host:port/path#fragment
        val atIndex = remaining.indexOf('@')
        if (atIndex == -1) throw InvalidUrlFormatException("VMess URL 缺少 @ 分隔符")

        val userInfo = remaining.substring(0, atIndex)
        // uuid is the first 36 chars (standard UUID format with hyphens)
        // aid is separated by the last '-' from the uuid
        val lastDashInUser = userInfo.lastIndexOf('-')
        val uuid: String
        val aid: Int
        if (lastDashInUser > 0 && userInfo.length - lastDashInUser <= 3 &&
            userInfo.substring(lastDashInUser + 1).all { it.isDigit() }) {
            uuid = userInfo.substring(0, lastDashInUser)
            aid = userInfo.substring(lastDashInUser + 1).toIntOrNull() ?: 0
        } else {
            uuid = userInfo
            aid = 0
        }

        val hostPart = remaining.substring(atIndex + 1)

        // Extract fragment (#tag) if present
        val fragmentIndex = hostPart.indexOf('#')
        val nodeName: String?
        val hostWithoutFragment: String
        if (fragmentIndex >= 0) {
            val rawFragment = hostPart.substring(fragmentIndex + 1)
            // If fragment contains @, use the first part as the display name
            nodeName = rawFragment.substringBefore("@").ifBlank { rawFragment }
            hostWithoutFragment = hostPart.substring(0, fragmentIndex)
        } else {
            nodeName = null
            hostWithoutFragment = hostPart
        }

        // Remove path from host:port
        val pathIndex = hostWithoutFragment.indexOf('/')
        val hostPort = if (pathIndex >= 0) hostWithoutFragment.substring(0, pathIndex) else hostWithoutFragment

        val colonIndex = hostPort.lastIndexOf(':')
        val host: String
        val port: Int
        if (colonIndex >= 0) {
            host = hostPort.substring(0, colonIndex)
            port = hostPort.substring(colonIndex + 1).toIntOrNull()
                ?: throw InvalidUrlFormatException("非法端口: ${hostPort.substring(colonIndex + 1)}")
        } else {
            host = hostPort
            port = 443
        }

        if (port !in 1..65535) throw InvalidUrlFormatException("端口超出范围: $port")

        return VmessParsed(host = host, port = port, uuid = uuid, aid = aid, nodeName = nodeName)
    }

    private data class VmessParsed(
        val host: String,
        val port: Int,
        val uuid: String,
        val aid: Int,
        val nodeName: String?,
    )
}
