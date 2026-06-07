package com.kebab.v2rayk.wrapper.config.bound.stream

/**
 * WebSocket 传输配置。
 *
 * @see <a href="https://www.v2fly.org/config/transport/websocket.html">WebSocket 文档</a>
 */

/**
 * WebSocket 传输设置。
 */
data class WebSocketSettings(
    val acceptProxyProtocol: Boolean = false,
    val path: String = "/",
    val headers: Map<String, String> = emptyMap(),
    val maxEarlyData: Int = 0,
    val useBrowserForwarding: Boolean = false,
    val earlyDataHeaderName: String? = null,
)
