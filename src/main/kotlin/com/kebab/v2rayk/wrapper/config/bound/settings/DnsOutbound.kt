package com.kebab.v2rayk.wrapper.config.bound.settings

import com.fasterxml.jackson.annotation.JsonValue

/**
 * DNS 出站协议配置。
 * DNS 出站协议主要用于拦截和转发 DNS 查询。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/dns.html">DNS 出站文档</a>
 */

/**
 * DNS 出站配置。
 */
data class DnsOutboundSettings(
    val network: DnsOutboundNetwork? = null,
    val address: String? = null,
    val port: Int? = null,
) : OutboundSettings()

enum class DnsOutboundNetwork(@JsonValue val networkName: String) {
    TCP("tcp"),
    UDP("udp"),
}
