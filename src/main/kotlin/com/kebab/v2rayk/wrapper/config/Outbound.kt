package com.kebab.v2rayk.wrapper.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.kebab.v2rayk.wrapper.config.bound.ProtocolType
import com.kebab.v2rayk.wrapper.config.bound.settings.*
import com.kebab.v2rayk.wrapper.config.bound.stream.StreamSettings

/**
 * 出站连接用于向远程网站或下一级代理服务器发送数据。
 *
 * @see <a href="https://www.v2fly.org/config/outbounds.html">Outbounds 文档</a>
 */
@JsonDeserialize(using = OutboundDeserializer::class)
data class Outbound(
    /**
     * 用于发送数据的 IP 地址，当主机有多个 IP 地址时有效，默认值为"0.0.0.0"。
     */
    val sendThrough: String = "0.0.0.0",
    /**
     * 连接协议名称，可选的值见协议列表。
     */
    val protocol: ProtocolType,
    /**
     * 具体的配置内容，视协议不同而不同。详见每个协议中的OutboundConfigurationObject。
     */
    val settings: OutboundSettings,
    /**
     * 此出站连接的标识，用于在其它的配置中定位此连接。当其值不为空时，必须在所有 tag 中唯一。
     */
    val tag: String? = null,
    /**
     * 底层传输配置
     */
    val streamSettings: StreamSettings? = null,
    /**
     * 出站代理配置。当出站代理生效时，此出站协议的streamSettings将不起作用。
     */
    val proxySettings: ProxySettings? = null,
    /**
     * Mux 配置。
     */
    val mux: Mux = Mux(),
)

data class ProxySettings(
    /**
     * 当指定另一个出站协议的标识时，此出站协议发出的数据，将被转发至所指定的出站协议发出。
     */
    val tag: String,
)

data class Mux(
    /**
     * 是否启用 Mux 转发请求
     */
    val enabled: Boolean = false,
    /**
     * 最大并发连接数。最小值1，最大值1024，缺省默认值8。
     * 特殊值-1，不加载mux模块。(4.22.0+)
     * 这个数值表示了一个 TCP 连接上最多承载的 Mux 连接数量。当客户端发出了 8 个 TCP 请求，而concurrency=8时，V2Ray 只会发出一条实际的 TCP 连接，客户端的 8 个请求全部由这个 TCP 连接传输。
     */
    val concurrency: Int = 8,
)

/**
 * Outbound 自定义反序列化器。
 * 根据 JSON 中的 [protocol] 字段选择正确的 [OutboundSettings] 子类进行反序列化。
 */
class OutboundDeserializer : JsonDeserializer<Outbound>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Outbound {
        val node: JsonNode = p.codec.readTree(p)

        val sendThrough = node.get("sendThrough")?.asText() ?: "0.0.0.0"
        val protocol = ProtocolType.valueOf(node["protocol"].asText().uppercase())
        val tag = node.get("tag")?.asText()

        // 根据 protocol 选择正确的 OutboundSettings 子类
        val settings: OutboundSettings = if (node.has("settings") && !node["settings"].isNull) {
            when (protocol) {
                ProtocolType.VMESS -> ctxt.readTreeAsValue(node["settings"], VmessOutboundSettings::class.java)
                ProtocolType.SHADOWSOCKS -> ctxt.readTreeAsValue(node["settings"], ShadowsocksOutboundSettings::class.java)
                ProtocolType.SOCKS -> ctxt.readTreeAsValue(node["settings"], SocksOutboundSettings::class.java)
                ProtocolType.HTTP -> ctxt.readTreeAsValue(node["settings"], HttpOutboundSettings::class.java)
                ProtocolType.TROJAN -> ctxt.readTreeAsValue(node["settings"], TrojanOutboundSettings::class.java)
                ProtocolType.VLESS -> ctxt.readTreeAsValue(node["settings"], VlessOutboundSettings::class.java)
                ProtocolType.FREEDOM -> ctxt.readTreeAsValue(node["settings"], FreedomOutboundSettings::class.java)
                ProtocolType.BLACKHOLE -> ctxt.readTreeAsValue(node["settings"], BlackholeOutboundSettings::class.java)
                ProtocolType.DNS -> ctxt.readTreeAsValue(node["settings"], DnsOutboundSettings::class.java)
                ProtocolType.LOOPBACK -> ctxt.readTreeAsValue(node["settings"], LoopbackOutboundSettings::class.java)
                ProtocolType.HYSTERIA2 -> ctxt.readTreeAsValue(node["settings"], Hysteria2OutboundSettings::class.java)
                else -> throw IllegalArgumentException("Unsupported outbound protocol: $protocol")
            }
        } else {
            throw IllegalArgumentException("Missing settings for outbound protocol: $protocol")
        }

        val streamSettings: StreamSettings? = if (node.has("streamSettings") && !node["streamSettings"].isNull) {
            ctxt.readTreeAsValue(node["streamSettings"], StreamSettings::class.java)
        } else null

        val proxySettings: ProxySettings? = if (node.has("proxySettings") && !node["proxySettings"].isNull) {
            ctxt.readTreeAsValue(node["proxySettings"], ProxySettings::class.java)
        } else null

        val mux: Mux = if (node.has("mux") && !node["mux"].isNull) {
            ctxt.readTreeAsValue(node["mux"], Mux::class.java)
        } else Mux()

        return Outbound(
            sendThrough = sendThrough,
            protocol = protocol,
            settings = settings,
            tag = tag,
            streamSettings = streamSettings,
            proxySettings = proxySettings,
            mux = mux,
        )
    }
}
