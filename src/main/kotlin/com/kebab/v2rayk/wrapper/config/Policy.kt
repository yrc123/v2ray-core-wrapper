package com.kebab.v2rayk.wrapper.config

/**
 * 本地策略
 *
 * 本地策略可以配置一些用户相关的权限，比如连接超时设置
 * V2Ray 处理的每一个连接，都对应到一个用户，按照这个用户的等级（level）应用不同的策略
 * 本地策略可按照等级的不同而变化
 */
data class Policy(
	/**
	 * 一组键值对，每个键是一个字符串形式的数字（JSON 的要求），
	 * 比如 "0"、"1" 等，双引号不能省略，这个数字对应用户等级
	 * 每一个值是一个 LevelPolicyObject.
	 */
	val levels: Map<String, Levels> = mutableMapOf(),
	/**
	 * V2Ray 系统的策略
	 */
	val system: System = System(),
)
data class System(
	/**
	 * 当值为true时，开启所有入站代理的上行流量统计。
	 */
	val statsInboundUplink: Boolean = false,
	/**
	 * 当值为true时，开启所有入站代理的下行流量统计。
	 */
	val statsInboundDownlink: Boolean = false,
)
data class Levels(
	/**
	 * 连接建立时的握手时间限制。单位为秒。默认值为4
	 * 在入站代理处理一个新连接时，在握手阶段（比如 VMess 读取头部数据，判断目标服务器地址）
	 * 如果使用的时间超过这个时间，则中断该连接。
	 */
	val handshake: Int = 4,
	/**
	 * 连接空闲的时间限制。单位为秒。默认值为300
	 * 在入站出站代理处理一个连接时，如果在 connIdle 时间内
	 * 没有任何数据被传输（包括上行和下行数据），则中断该连接。
	 */
	val connIdle: Int = 300,
	/**
	 * 当连接下行线路关闭后的时间限制。单位为秒。默认值为2
	 * 当服务器（如远端网站）关闭下行连接时，出站代理会在等待 uplinkOnly 时间后中断连接。
	 */
	val uplinkOnly: Int = 2,
	/**
	 * 当连接上行线路关闭后的时间限制。单位为秒。默认值为5。
	 * 当客户端（如浏览器）关闭上行连接时，入站代理会在等待 downlinkOnly 时间后中断连接。
	 * 在 HTTP 浏览的场景中，可以将uplinkOnly和downlinkOnly设为0，以提高连接关闭的效率。
	 */
	val downlinkOnly: Int = 5,
	/**
	 * 当值为true时，开启当前等级的所有用户的上行流量统计。
	 */
	val statsUserUplink: Boolean = false,
	/**
	 * 当值为true时，开启当前等级的所有用户的下行流量统计。
	 */
	val statsUserDownlink: Boolean = false,
	/**
	 * 每个连接的内部缓存大小。单位为 kB。当值为0时，内部缓存被禁用。
	 *
	 * 默认值 (V2Ray 4.4+):
	 * 	在 ARM、MIPS、MIPSLE 平台上，默认值为0。
	 * 	在 ARM64、MIPS64、MIPS64LE 平台上，默认值为4。
	 * 	在其它平台上，默认值为512。
	 * 默认值 (V2Ray 4.3-):
	 * 	在 ARM、MIPS、MIPSLE、ARM64、MIPS64、MIPS64LE 平台上，默认值为16。
	 * 	在其它平台上，默认值为2048。
	 */
	val bufferSize: Int? = null,
)
