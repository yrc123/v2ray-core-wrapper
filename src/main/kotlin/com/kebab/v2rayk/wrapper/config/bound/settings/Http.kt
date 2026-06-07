package com.kebab.v2rayk.wrapper.config.bound.settings

/**
 * HTTP 代理协议配置（入站 + 出站）。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/http.html">HTTP 文档</a>
 */

// ==================== 出站配置 ====================

/**
 * HTTP 出站配置。
 */
data class HttpOutboundSettings(
    val servers: List<HttpServer>,
) : OutboundSettings()

/**
 * HTTP 代理服务器配置（出站用）。
 */
data class HttpServer(
    val address: String,
    val port: Int,
    val users: List<HttpAccount> = emptyList(),
)

// ==================== 入站配置 ====================

/**
 * HTTP 入站配置。
 */
data class HttpInboundSettings(
    val timeout: Int = 300,
    val accounts: List<HttpAccount> = emptyList(),
    val allowTransparent: Boolean = false,
    val userLevel: Int = 0,
) : InboundSettings()

/**
 * HTTP 账号（入站/出站共用）。
 */
data class HttpAccount(
    val user: String,
    val pass: String,
)
