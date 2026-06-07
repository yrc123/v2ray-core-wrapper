package com.kebab.v2rayk.wrapper.config.bound.stream

import com.fasterxml.jackson.annotation.JsonValue

/**
 * TLS 配置。
 *
 * @see <a href="https://www.v2fly.org/config/transport.html#tlsobject">TLS 文档</a>
 */

/**
 * TLS 设置。
 */
data class TlsSettings(
    val serverName: String? = null,
    val alpn: List<String> = listOf("h2", "http/1.1"),
    val allowInsecure: Boolean = false,
    val disableSystemRoot: Boolean = false,
    val certificates: List<CertificateObject> = emptyList(),
    val verifyClientCertificate: Boolean = false,
    val pinnedPeerCertificateChainSha256: List<String> = emptyList(),
)

/**
 * TLS 证书配置。
 * `certificateFile` 和 `certificate` 二选一，`keyFile` 和 `key` 二选一。
 */
data class CertificateObject(
    val usage: CertificateUsage = CertificateUsage.ENCIPHERMENT,
    val certificateFile: String? = null,
    val certificate: List<String>? = null,
    val keyFile: String? = null,
    val key: List<String>? = null,
)

/**
 * TLS 证书用途。
 */
enum class CertificateUsage(@JsonValue val usageName: String) {
    ENCIPHERMENT("encipherment"),
    VERIFY("verify"),
    ISSUE("issue"),
    VERIFY_CLIENT("verifyclient"),
}
