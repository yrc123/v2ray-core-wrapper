package com.kebab.v2rayk.wrapper.config.bound.stream

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

/**
 * Hysteria2 传输层配置（用于搭配其他协议时作为传输层使用）。
 *
 * @see <a href="https://www.v2fly.org/config/transport/hy2.html">Hysteria2 Transport 文档</a>
 */

/**
 * Hysteria2 传输层设置。
 */
data class Hysteria2TransportSettings(
    val password: String? = null,
    @JsonProperty("use_udp_extension")
    val useUdpExtension: Boolean = false,
    val congestion: Congestion = Congestion(),
)

/**
 * Hysteria2 拥塞控制配置。
 */
data class Congestion(
    val type: CongestionType = CongestionType.BBR,
    @JsonProperty("up_mbps")
    val upMbps: Int? = null,
    @JsonProperty("down_mbps")
    val downMbps: Int? = null,
)

/**
 * Hysteria2 拥塞控制算法类型。
 */
enum class CongestionType(@JsonValue val typeName: String) {
    BBR("bbr"),
    BRUTAL("brutal"),
}
