package com.kebab.v2rayk.wrapper.config.bound

import com.fasterxml.jackson.annotation.JsonValue

/**
 * V2Ray 协议类型枚举。
 * 所有值均使用 V2Ray JSON 配置中的小写协议名。
 */
enum class ProtocolType(@JsonValue val protocolName: String) {
    BLACKHOLE("blackhole"),
    DNS("dns"),
    DOKODEMO("dokodemo-door"),
    FREEDOM("freedom"),
    HTTP("http"),
    HYSTERIA2("hysteria2"),
    LOOPBACK("loopback"),
    MTPROTO("mtproto"),
    SHADOWSOCKS("shadowsocks"),
    SOCKS("socks"),
    TROJAN("trojan"),
    VLESS("vless"),
    VMESS("vmess"),
}