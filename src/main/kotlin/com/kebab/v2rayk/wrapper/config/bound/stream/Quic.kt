package com.kebab.v2rayk.wrapper.config.bound.stream

import com.fasterxml.jackson.annotation.JsonValue

/**
 * QUIC 传输配置。
 *
 * @see <a href="https://www.v2fly.org/config/transport/quic.html">QUIC 文档</a>
 */

/**
 * QUIC 传输设置。
 */
data class QuicSettings(
    val security: QuicSecurity = QuicSecurity.NONE,
    val key: String? = null,
    val header: QuicHeader = QuicHeader(),
)

/**
 * QUIC 加密方式。
 */
enum class QuicSecurity(@JsonValue val securityName: String) {
    NONE("none"),
    AES_128_GCM("aes-128-gcm"),
    CHACHA20_POLY1305("chacha20-poly1305"),
}

/**
 * QUIC 数据包头部伪装配置。
 */
data class QuicHeader(
    val type: PacketHeaderType = PacketHeaderType.NONE,
)
