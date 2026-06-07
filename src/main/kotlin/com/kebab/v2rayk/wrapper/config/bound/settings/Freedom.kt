package com.kebab.v2rayk.wrapper.config.bound.settings

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Freedom 出站协议配置。
 * Freedom 是一个出站协议，用来向任意网络发送正常的 TCP 或 UDP 数据。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/freedom.html">Freedom 文档</a>
 */

/**
 * Freedom 出站配置。
 */
data class FreedomOutboundSettings(
    val domainStrategy: FreedomDomainStrategy = FreedomDomainStrategy.AS_IS,
    val redirect: String? = null,
    val userLevel: Int = 0,
) : OutboundSettings()

enum class FreedomDomainStrategy(@JsonValue val strategyName: String) {
    AS_IS("AsIs"),
    USE_IP("UseIP"),
    USE_IPV4("UseIPv4"),
    USE_IPV6("UseIPv6"),
}
