package com.kebab.v2rayk.wrapper.config

import com.kebab.v2rayk.wrapper.config.bound.Settings
import com.kebab.v2rayk.wrapper.config.bound.StreamSettings

/**
 * 出站连接用于向远程网站或下一级代理服务器发送数据，可用的协议请见协议列表。
 */
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
	val settings: Settings,
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
	val mux: Mux = Mux()
)
data class ProxySettings(
	/**
	 * 当指定另一个出站协议的标识时，此出站协议发出的数据，将被转发至所指定的出站协议发出。
	 */
	val tag: String
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
