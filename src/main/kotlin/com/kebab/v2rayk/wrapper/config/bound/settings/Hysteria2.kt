package com.kebab.v2rayk.wrapper.config.bound.settings

/**
 * Hysteria2 协议配置（入站 + 出站）。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/hy2.html">Hysteria2 文档</a>
 */

// ==================== 出站配置 ====================

/**
 * Hysteria2 出站配置。
 */
data class Hysteria2OutboundSettings(
    val servers: List<Hysteria2Server>,
) : OutboundSettings()

/**
 * Hysteria2 服务器配置。
 */
data class Hysteria2Server(
    val address: String,
    val port: Int,
)

// ==================== 入站配置 ====================

/**
 * Hysteria2 入站配置（无需额外配置）。
 */
class Hysteria2InboundSettings : InboundSettings()
