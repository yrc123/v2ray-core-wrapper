package com.kebab.v2rayk.wrapper.config

import com.fasterxml.jackson.annotation.JsonValue

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
	 */
	var listen: String = "0.0.0.0",
	/**
	 * 连接协议名称
	 */
	var protocol: Protocol,
	/**
	 * 具体的配置内容，视协议不同而不同。详见每个协议中的InboundConfigurationObject。
	 */
	var settings: Settings,// TODO: 重写类型
	/**
	 * 底层传输配置
	 */
	var streamSettings: StreamSettings,// TODO: 重写类型
	/**
	 * 此入站连接的标识，用于在其它的配置中定位此连接。当其不为空时，其值必须在所有tag中唯一。
	 */
	var tag: String,
	/**
	 * 流量监控配置
	 */
	var sniffing: Sniffing = Sniffing(),
	/**
	 * 端口分配配置
	 */
	var allocate: Allocate,
)
data class Settings(
	var any: Any? = null
)
data class Sniffing(
	/**
	 * 是否开启流量探测。
	 */
	var enabled: Boolean = false,
	/**
	 * 当流量为指定类型时，按其中包括的目标地址重置当前连接的目标。
	 */
	var destOverride: List<SniffingType> = listOf(),
)
data class StreamSettings(
	var any: Any? = null
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
enum class Protocol(@JsonValue val protocolName: String) {
	BLACKHOLE("Blackhole"),
	DOKODEMO_DOOR("Dokodemo-door"),
	FREEDOM("Freedom"),
	HTTP("HTTP"),
	MTPROTO("MTProto"),
	SHADOWSOCKS("Shadowsocks"),
	SOCKS("Socks"),
	VMESS("VMess"),
}
enum class SniffingType(@JsonValue val sniffingName: String) {
	HTTP("http"),
	TLS("tls"),
}
enum class AllocateStrategy(@JsonValue val strategyName: String) {
	ALWAYS("always"),
	RANDOM("random"),
}
