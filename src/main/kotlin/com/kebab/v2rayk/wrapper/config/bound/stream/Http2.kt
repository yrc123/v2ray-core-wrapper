package com.kebab.v2rayk.wrapper.config.bound.stream

/**
 * HTTP/2 传输配置。
 *
 * @see <a href="https://www.v2fly.org/config/transport/h2.html">HTTP/2 文档</a>
 */

/**
 * HTTP/2 传输设置。
 */
data class Http2Settings(
    val host: List<String> = emptyList(),
    val path: String = "/",
    val method: String = "PUT",
    val headers: Map<String, List<String>> = emptyMap(),
)
