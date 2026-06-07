package com.kebab.v2rayk.wrapper.config.bound.settings

import com.fasterxml.jackson.annotation.JsonValue

/**
 * SOCKS 协议配置（入站 + 出站）。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/socks.html">SOCKS 文档</a>
 */

// ==================== 出站配置 ====================

/**
 * SOCKS 出站配置。
 */
data class SocksOutboundSettings(
    val servers: List<SocksServer>,
    val version: SocksVersion? = null,
) : OutboundSettings()

/**
 * SOCKS 服务器配置（出站用）。
 */
data class SocksServer(
    val address: String,
    val port: Int,
    val users: List<SocksUser> = emptyList(),
)

/**
 * SOCKS 用户配置（出站用）。
 */
data class SocksUser(
    val user: String,
    val pass: String,
    val level: Int = 0,
)

enum class SocksVersion(@JsonValue val version: String) {
    V5("5"),
    V4A("4a"),
    V4("4"),
}

// ==================== 入站配置 ====================

/**
 * SOCKS 入站配置。
 */
data class SocksInboundSettings(
    val auth: SocksAuth = SocksAuth.NOAUTH,
    val accounts: List<SocksAccount> = emptyList(),
    val udp: Boolean = false,
    val ip: String? = null,
    val userLevel: Int = 0,
    val packetEncoding: PacketEncoding = PacketEncoding.NONE,
) : InboundSettings()

enum class SocksAuth(@JsonValue val authName: String) {
    NOAUTH("noauth"),
    PASSWORD("password"),
}

/**
 * SOCKS 用户账号（入站用）。
 */
data class SocksAccount(
    val user: String,
    val pass: String,
)
