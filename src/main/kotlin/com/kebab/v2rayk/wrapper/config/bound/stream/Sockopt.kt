package com.kebab.v2rayk.wrapper.config.bound.stream

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Sockopt 透明代理配置。
 *
 * @see <a href="https://www.v2fly.org/config/transport.html#sockoptobject">Sockopt 文档</a>
 */

/**
 * Sockopt 设置。
 */
data class SockoptSettings(
    val mark: Int = 0,
    val tcpFastOpen: Boolean? = null,
    val tcpFastOpenQueueLength: Int = 4096,
    val tproxy: TproxyMode = TproxyMode.OFF,
    val tcpKeepAliveInterval: Int = 0,
    val bindToDevice: String? = null,
    val mptcp: Boolean? = null,
)

/**
 * 透明代理模式。
 */
enum class TproxyMode(@JsonValue val modeName: String) {
    REDIRECT("redirect"),
    TPROXY("tproxy"),
    OFF("off"),
}
