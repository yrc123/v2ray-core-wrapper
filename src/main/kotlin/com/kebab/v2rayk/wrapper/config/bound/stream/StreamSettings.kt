package com.kebab.v2rayk.wrapper.config.bound.stream

import com.fasterxml.jackson.annotation.JsonValue

/**
 * 分协议传输配置，对应 Inbound/Outbound 中的 `streamSettings` 字段。
 * 非多态扁平结构——由 [network] 字段声明当前使用的传输方式，其余字段为可选。
 *
 * @see <a href="https://www.v2fly.org/config/transport.html">Transport 文档</a>
 */
data class StreamSettings(
    val network: TransportNetwork = TransportNetwork.TCP,
    val security: TransportSecurity = TransportSecurity.NONE,
    val tlsSettings: TlsSettings? = null,
    val tcpSettings: TcpSettings? = null,
    val kcpSettings: KcpSettings? = null,
    val wsSettings: WebSocketSettings? = null,
    val httpSettings: Http2Settings? = null,
    val quicSettings: QuicSettings? = null,
    val dsSettings: DomainSocketSettings? = null,
    val grpcSettings: GrpcSettings? = null,
    val sockopt: SockoptSettings? = null,
)

/**
 * 传输层网络类型。
 */
enum class TransportNetwork(@JsonValue val networkName: String) {
    TCP("tcp"),
    KCP("kcp"),
    WS("ws"),
    HTTP("http"),
    DOMAINSOCKET("domainsocket"),
    QUIC("quic"),
    GRPC("grpc"),
}

/**
 * 传输层安全类型。
 */
enum class TransportSecurity(@JsonValue val securityName: String) {
    NONE("none"),
    TLS("tls"),
}
