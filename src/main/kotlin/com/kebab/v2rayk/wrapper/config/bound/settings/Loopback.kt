package com.kebab.v2rayk.wrapper.config.bound.settings

/**
 * Loopback 出站协议配置。
 * Loopback 可使出站连接被重新路由。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/loopback.html">Loopback 文档</a>
 */

/**
 * Loopback 出站配置。
 */
data class LoopbackOutboundSettings(
    val inboundTag: String,
) : OutboundSettings()
