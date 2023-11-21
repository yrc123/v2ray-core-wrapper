package com.kebab.v2rayk.wrapper.config

/**
 * 反向代理
 *
 * 反向代理是一个 V2Ray 的附加功能，可以把服务器端的流量向客户端转发，即逆向流量转发。
 * 反向代理功能在 V2Ray 4.0+ 可用。目前处于测试阶段，可能会有一些问题。
 *
 * 反向代理的大致工作原理如下:
 *
 * - 假设在主机 A 中有一个网页服务器，这台主机没有公网 IP，无法在公网上直接访问。
 *   另有一台主机 B，它可以由公网访问。现在我们需要把 B 作为入口，把流量从 B 转发到 A。
 * - 在主机 A 中配置一个 V2Ray，称为bridge，在 B 中也配置一个 V2Ray，称为portal。
 * - bridge会向portal主动建立连接，此连接的目标地址可以自行设定。
 *   portal会收到两种连接，一是由bridge发来的连接，二是公网用户发来的连接。portal会自动将两类连接合并。于是bridge就可以收到公网流量了。
 * - bridge在收到公网流量之后，会将其原封不动地发给主机 A 中的网页服务器。当然，这一步需要路由的协作。
 * - bridge会根据流量的大小进行动态的负载均衡。
 *
 * 反向代理默认已开启 Mux，请不要在其用到的出站代理上再次开启 Mux。
 * @see <a href="https://www.v2ray.com/chapter_02/reverse.html">v2ray反向代理配置</a>
 */
data class Reverse(
	/**
	 * 一个数组，每一项表示一个bridge。每个bridge的配置是一个 BridgeObject。
	 */
	val bridges: List<BridgesItem?>? = null,
	/**
	 * 一个数组，每一项表示一个portal。每个portal的配置是一个 PortalObject。
	 */
	val portals: List<PortalsItem?>? = null
)

/**
 *
 * 和其它配置一样，一个 V2Ray 既可以作为bridge，也可以作为portal，也可以同时两者，以适用于不同的场景需要。
 */
data class PortalsItem(
	/**
	 * 一个标识，所有由bridge发出的连接，都会带有这个标识。可以在路由中使用inboundTag进行识别。
	 */
	val tag: String,
	/**
	 * 一个域名。bridge向portal建立的连接，都会使用这个域名进行发送。
	 * 这个域名只作为bridge和portal的通信用途，不必真实存在。
	 */
	val domain: String
)
data class BridgesItem(
	/**
	 * portal的标识。在路由中使用outboundTag将流量转发到这个portal。
	 */
	val tag: String,
	/**
	 * 一个域名。当portal接收到流量时，如果流量的目标域名是此域名，则portal认为当前连接上bridge发来的通信连接。
	 * 而其它流量则会被当成需要转发的流量。portal所做的工作就是把这两类连接进行识别并拼接。
	 */
	val domain: String
)
