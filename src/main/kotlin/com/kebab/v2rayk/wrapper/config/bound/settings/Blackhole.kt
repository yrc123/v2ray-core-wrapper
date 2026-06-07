package com.kebab.v2rayk.wrapper.config.bound.settings

import com.fasterxml.jackson.annotation.JsonValue

/**
 * Blackhole 出站协议配置。
 * Blackhole（黑洞）会阻碍所有数据的出站，配合路由使用可达到禁止访问某些网站的效果。
 *
 * @see <a href="https://www.v2fly.org/config/protocols/blackhole.html">Blackhole 文档</a>
 */

/**
 * Blackhole 出站配置。
 */
data class BlackholeOutboundSettings(
    val response: BlackholeResponse? = null,
) : OutboundSettings()

/**
 * Blackhole 响应配置。
 */
data class BlackholeResponse(
    val type: BlackholeResponseType = BlackholeResponseType.NONE,
)

enum class BlackholeResponseType(@JsonValue val typeName: String) {
    NONE("none"),
    HTTP("http"),
}
