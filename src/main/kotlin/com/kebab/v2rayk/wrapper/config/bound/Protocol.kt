package com.kebab.v2rayk.wrapper.config.bound

import com.fasterxml.jackson.annotation.JsonValue

enum class ProtocolType (@JsonValue val protocolName: String) {
    BLACKHOLE("Blackhole"),
    DNS("DNS"),
    DOKODEMO("Dokodemo"),
    FREEDOM("Freedom"),
    HTTP("HTTP"),
    MTPROTO("MTProto"),
    SHADOWSOCKS("Shadowsocks"),
    SOCKS("SOCKS"),
    VMESS("VMess"),
}