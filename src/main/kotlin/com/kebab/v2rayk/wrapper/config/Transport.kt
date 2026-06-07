package com.kebab.v2rayk.wrapper.config

import com.kebab.v2rayk.wrapper.config.bound.stream.*

/**
 * 全局传输配置，对应配置文件的 `transport` 项。
 * 当分协议 `streamSettings` 未指定具体传输方式参数时，使用此全局配置的值。
 *
 * @see <a href="https://www.v2fly.org/config/transport.html">Transport 文档</a>
 */
data class Transport(
    val tcpSettings: TcpSettings? = null,
    val kcpSettings: KcpSettings? = null,
    val wsSettings: WebSocketSettings? = null,
    val httpSettings: Http2Settings? = null,
    val quicSettings: QuicSettings? = null,
    val dsSettings: DomainSocketSettings? = null,
    val grpcSettings: GrpcSettings? = null,
    val hy2steriaSettings: Hysteria2TransportSettings? = null,
)
