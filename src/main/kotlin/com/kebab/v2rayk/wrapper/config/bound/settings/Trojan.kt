package com.kebab.v2rayk.wrapper.config.bound.settings

/**
 * Trojan 协议配置（入站 + 出站）。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/trojan.html">Trojan 文档</a>
 */

// ==================== 出站配置 ====================

/**
 * Trojan 出站配置。
 */
data class TrojanOutboundSettings(
    val servers: List<TrojanServer>,
) : OutboundSettings()

/**
 * Trojan 服务器配置（出站用）。
 */
data class TrojanServer(
    val address: String,
    val port: Int,
    val password: String,
    val email: String? = null,
    val level: Int = 0,
)

// ==================== 入站配置 ====================

/**
 * Trojan 入站配置。
 */
data class TrojanInboundSettings(
    val clients: List<TrojanClient>,
    val fallbacks: List<TrojanFallback> = emptyList(),
    val packetEncoding: PacketEncoding = PacketEncoding.NONE,
) : InboundSettings()

/**
 * Trojan 入站客户端配置。
 */
data class TrojanClient(
    val password: String,
    val email: String? = null,
    val level: Int = 0,
)

/**
 * Trojan/VLESS Fallback 回落配置。
 */
data class TrojanFallback(
    val alpn: String? = null,
    val path: String? = null,
    val dest: Any,  // 可以是 String (addr:port) 或 Int (port)
    val xver: Int = 0,
)
