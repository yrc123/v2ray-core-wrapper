package com.kebab.v2rayk.wrapper.config.bound.settings

import com.fasterxml.jackson.annotation.JsonValue

/**
 * VMess 协议配置（入站 + 出站）。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/vmess.html">VMess 文档</a>
 */

// ==================== 出站配置 ====================

/**
 * VMess 出站配置。
 */
data class VmessOutboundSettings(
    val vnext: List<VmessServer>,
) : OutboundSettings()

/**
 * VMess 出站服务器配置。
 */
data class VmessServer(
    val address: String,
    val port: Int,
    val users: List<VmessUser>,
)

/**
 * VMess 出站用户配置。
 */
data class VmessUser(
    val id: String,
    val alterId: Int = 0,
    val security: VmessSecurity = VmessSecurity.AUTO,
    val level: Int = 0,
    val experiments: String? = null,
)

enum class VmessSecurity(@JsonValue val securityName: String) {
    AES_128_GCM("aes-128-gcm"),
    CHACHA20_POLY1305("chacha20-poly1305"),
    AUTO("auto"),
    NONE("none"),
    ZERO("zero"),
}

// ==================== 入站配置 ====================

/**
 * VMess 入站配置。
 */
data class VmessInboundSettings(
    val clients: List<VmessClient>,
    val default: VmessDefault? = null,
    val detour: VmessDetour? = null,
    val disableInsecureEncryption: Boolean = false,
) : InboundSettings()

/**
 * VMess 入站客户端配置。
 */
data class VmessClient(
    val id: String,
    val level: Int = 0,
    val alterId: Int = 0,
    val email: String? = null,
)

/**
 * VMess 默认客户端配置（配合 detour 使用）。
 */
data class VmessDefault(
    val level: Int = 0,
    val alterId: Int = 0,
)

/**
 * VMess Detour 配置，指向另一个 VMess 入站的 tag。
 */
data class VmessDetour(
    val to: String,
)
