package com.kebab.v2rayk.wrapper.config.bound.stream

/**
 * gRPC 传输配置。
 *
 * @see <a href="https://www.v2fly.org/config/transport/grpc.html">gRPC 文档</a>
 */

/**
 * gRPC 传输设置。
 */
data class GrpcSettings(
    val serviceName: String = "GunService",
)
