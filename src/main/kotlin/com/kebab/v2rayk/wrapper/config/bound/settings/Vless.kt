package com.kebab.v2rayk.wrapper.config.bound.settings

/**
 * VLESS 协议配置（入站 + 出站）。
 * 注意：VLESS 已被标记为弃用，推荐使用 Trojan 替代。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/vless.html">VLESS 文档</a>
 */

// ==================== 出站配置 ====================

/**
 * VLESS 出站配置。
 */
data class VlessOutboundSettings(
    val vnext: List<VlessServer>,
) : OutboundSettings()

/**
 * VLESS 服务器配置（出站用）。
 */
data class VlessServer(
    val address: String,
    val port: Int,
    val users: List<VlessUser>,
)

/**
 * VLESS 出站用户配置。
 */
data class VlessUser(
    val id: String,
    val encryption: String = "none",
    val level: Int = 0,
)

// ==================== 入站配置 ====================

/**
 * VLESS 入站配置。
 */
data class VlessInboundSettings(
    val clients: List<VlessClient>,
    val decryption: String = "none",
    val fallbacks: List<TrojanFallback> = emptyList(),
) : InboundSettings()

/**
 * VLESS 入站客户端配置。
 */
data class VlessClient(
    val id: String,
    val level: Int = 0,
    val email: String? = null,
)
