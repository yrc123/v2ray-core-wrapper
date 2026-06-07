package com.kebab.v2rayk.wrapper.config.bound.stream

/**
 * Domain Socket 传输配置。
 *
 * @see <a href="https://www.v2fly.org/config/transport/domainsocket.html">Domain Socket 文档</a>
 */

/**
 * Domain Socket 传输设置。
 */
data class DomainSocketSettings(
    val path: String,
    val abstract: Boolean = false,
    val padding: Boolean = false,
)
