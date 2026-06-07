package com.kebab.v2rayk.wrapper.config.bound.stream

/**
 * mKCP 传输配置。
 *
 * @see <a href="https://www.v2fly.org/config/transport/mkcp.html">mKCP 文档</a>
 */

/**
 * mKCP 传输设置。
 */
data class KcpSettings(
    val mtu: Int = 1350,
    val tti: Int = 50,
    val uplinkCapacity: Int = 5,
    val downlinkCapacity: Int = 20,
    val congestion: Boolean = false,
    val readBufferSize: Int = 2,
    val writeBufferSize: Int = 2,
    val header: KcpHeader = KcpHeader(),
    val seed: String? = null,
)

/**
 * mKCP 数据包头部伪装配置。
 */
data class KcpHeader(
    val type: PacketHeaderType = PacketHeaderType.NONE,
)
