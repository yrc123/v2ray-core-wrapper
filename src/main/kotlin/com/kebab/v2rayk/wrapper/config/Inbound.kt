package com.kebab.v2rayk.wrapper.config

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.kebab.v2rayk.wrapper.config.bound.ProtocolType
import com.kebab.v2rayk.wrapper.config.bound.settings.*
import com.kebab.v2rayk.wrapper.config.bound.stream.StreamSettings

/**
 * 入站连接配置。
 *
 * @see <a href="https://www.v2fly.org/config/inbounds.html">Inbounds 文档</a>
 */
@JsonDeserialize(using = InboundDeserializer::class)
data class Inbound(
    /**
     * 端口。接受的格式如下:
     *
     * 整型数值: 实际的端口号。
     * 环境变量: 以"env:"开头，后面是一个环境变量的名称，如"env:PORT"。V2Ray 会以字符串形式解析这个环境变量。
     * 字符串: 可以是一个数值类型的字符串，如"1234"；或者一个数值范围，如"5-10"表示端口 5 到端口 10 这 6 个端口。
     * 当只有一个端口时，V2Ray 会在此端口监听入站连接。当指定了一个端口范围时，取决于allocate设置。
     */
    var port: String,
    /**
     * 监听地址，只允许 IP 地址，默认值为"0.0.0.0"，表示接收所有网卡上的连接。除此之外，必须指定一个现有网卡的地址。
     * v4.32.0+ 支持填写 Unix domain socket 路径。
     */
    var listen: String = "0.0.0.0",
    /**
     * 连接协议名称
     */
    var protocol: ProtocolType,
    /**
     * 具体的配置内容，视协议不同而不同。详见每个协议中的InboundConfigurationObject。
     */
    var settings: InboundSettings,
    /**
     * 底层传输配置
     */
    var streamSettings: StreamSettings? = null,
    /**
     * 此入站连接的标识，用于在其它的配置中定位此连接。当其不为空时，其值必须在所有tag中唯一。
     */
    var tag: String? = null,
    /**
     * 流量监控配置
     */
    var sniffing: Sniffing = Sniffing(),
    /**
     * 端口分配配置
     */
    var allocate: Allocate = Allocate(),
)

data class Sniffing(
    /**
     * 是否开启流量探测。
     */
    var enabled: Boolean = false,
    /**
     * 当流量为指定类型时，按其中包括的目标地址重置当前连接的目标。
     */
    var destOverride: List<SniffingType> = mutableListOf(),
    /**
     * 是否仅使用元数据推断目标地址而不截取流量内容。
     */
    var metadataOnly: Boolean = false,
)

data class Allocate(
    /**
     * 端口分配策略。
     * "always"表示总是分配所有已指定的端口，port中指定了多少个端口，V2Ray 就会监听这些端口
     * "random"表示随机开放端口，每隔refresh分钟在port范围中随机选取concurrency个端口来监听。
     */
    var strategy: AllocateStrategy = AllocateStrategy.ALWAYS,
    /**
     * 随机端口刷新间隔，单位为分钟。
     * 最小值为2，建议值为5。这个属性仅当strategy = random时有效。
     */
    var refresh: Int = 5,
    /**
     * 随机端口数量。最小值为1，最大值为port范围的三分之一。建议值为3
     */
    var concurrency: Int = 3,
)

enum class SniffingType(@JsonValue val sniffingName: String) {
    HTTP("http"),
    TLS("tls"),
    QUIC("quic"),
    FAKEDNS("fakedns"),
    FAKEDNS_OTHERS("fakedns+others"),
}

enum class AllocateStrategy(@JsonValue val strategyName: String) {
    ALWAYS("always"),
    RANDOM("random"),
}

/**
 * Inbound 自定义反序列化器。
 * 根据 JSON 中的 [protocol] 字段选择正确的 [InboundSettings] 子类进行反序列化。
 */
class InboundDeserializer : JsonDeserializer<Inbound>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Inbound {
        val node: JsonNode = p.codec.readTree(p)

        val port = node["port"].asText()
        val listen = node.get("listen")?.asText() ?: "0.0.0.0"
        val protocol = ProtocolType.valueOf(node["protocol"].asText().uppercase())
        val tag = node.get("tag")?.asText()

        // 根据 protocol 选择正确的 InboundSettings 子类
        val settings: InboundSettings = if (node.has("settings") && !node["settings"].isNull) {
            when (protocol) {
                ProtocolType.VMESS -> ctxt.readTreeAsValue(node["settings"], VmessInboundSettings::class.java)
                ProtocolType.SHADOWSOCKS -> ctxt.readTreeAsValue(node["settings"], ShadowsocksInboundSettings::class.java)
                ProtocolType.SOCKS -> ctxt.readTreeAsValue(node["settings"], SocksInboundSettings::class.java)
                ProtocolType.HTTP -> ctxt.readTreeAsValue(node["settings"], HttpInboundSettings::class.java)
                ProtocolType.TROJAN -> ctxt.readTreeAsValue(node["settings"], TrojanInboundSettings::class.java)
                ProtocolType.VLESS -> ctxt.readTreeAsValue(node["settings"], VlessInboundSettings::class.java)
                ProtocolType.DOKODEMO -> ctxt.readTreeAsValue(node["settings"], DokodemoInboundSettings::class.java)
                ProtocolType.HYSTERIA2 -> Hysteria2InboundSettings()
                else -> throw IllegalArgumentException("Unsupported inbound protocol: $protocol")
            }
        } else {
            when (protocol) {
                ProtocolType.HYSTERIA2 -> Hysteria2InboundSettings()
                else -> throw IllegalArgumentException("Missing settings for inbound protocol: $protocol")
            }
        }

        val streamSettings: StreamSettings? = if (node.has("streamSettings") && !node["streamSettings"].isNull) {
            ctxt.readTreeAsValue(node["streamSettings"], StreamSettings::class.java)
        } else null

        val sniffing: Sniffing = if (node.has("sniffing") && !node["sniffing"].isNull) {
            ctxt.readTreeAsValue(node["sniffing"], Sniffing::class.java)
        } else Sniffing()

        val allocate: Allocate = if (node.has("allocate") && !node["allocate"].isNull) {
            ctxt.readTreeAsValue(node["allocate"], Allocate::class.java)
        } else Allocate()

        return Inbound(
            port = port,
            listen = listen,
            protocol = protocol,
            settings = settings,
            streamSettings = streamSettings,
            tag = tag,
            sniffing = sniffing,
            allocate = allocate,
        )
    }
}
