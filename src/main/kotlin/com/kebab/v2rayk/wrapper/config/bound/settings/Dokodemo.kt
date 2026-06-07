package com.kebab.v2rayk.wrapper.config.bound.settings

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Dokodemo-door（任意门）入站协议配置。
 * Dokodemo door 可以监听一个本地端口，把所有进入此端口的数据发送至指定服务器的一个端口。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/dokodemo.html">Dokodemo 文档</a>
 */

/**
 * Dokodemo 入站配置。
 */
data class DokodemoInboundSettings(
    val address: String? = null,
    val port: Int,
    val network: DokodemoNetwork = DokodemoNetwork.TCP,
    val timeout: Int = 300,
    val followRedirect: Boolean = false,
    val userLevel: Int = 0,
) : InboundSettings()

enum class DokodemoNetwork(@JsonValue val networkName: String) {
    TCP("tcp"),
    UDP("udp"),
    TCP_UDP("tcp,udp"),
}
