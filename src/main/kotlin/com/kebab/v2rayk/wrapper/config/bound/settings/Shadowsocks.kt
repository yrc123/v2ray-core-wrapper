package com.kebab.v2rayk.wrapper.config.bound.settings

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Shadowsocks 协议配置（入站 + 出站）。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/shadowsocks.html">Shadowsocks 文档</a>
 */

// ==================== 出站配置 ====================

/**
 * Shadowsocks 出站配置。
 */
data class ShadowsocksOutboundSettings(
    val servers: List<ShadowsocksServer>,
) : OutboundSettings()

/**
 * Shadowsocks 服务器配置（出站用）。
 */
data class ShadowsocksServer(
    val email: String? = null,
    val address: String,
    val port: Int,
    val method: String,
    val password: String,
    val level: Int = 0,
    val ivCheck: Boolean = false,
)

// ==================== 入站配置 ====================

/**
 * Shadowsocks 入站配置。
 */
data class ShadowsocksInboundSettings(
    val email: String? = null,
    val method: String,
    val password: String,
    val level: Int = 0,
    val network: ShadowsocksNetwork = ShadowsocksNetwork.TCP,
    val ivCheck: Boolean = false,
    val udp: Boolean = false,
    val packetEncoding: PacketEncoding = PacketEncoding.NONE,
) : InboundSettings()

enum class ShadowsocksNetwork(@JsonValue val networkName: String) {
    TCP("tcp"),
    UDP("udp"),
    TCP_UDP("tcp,udp"),
}

/**
 * UDP 包编码方式。
 */
enum class PacketEncoding(@JsonValue val encodingName: String) {
    NONE("None"),
    PACKET("Packet"),
}
