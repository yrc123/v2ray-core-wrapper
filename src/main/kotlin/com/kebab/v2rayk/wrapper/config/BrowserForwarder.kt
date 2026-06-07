package com.kebab.v2rayk.wrapper.config

/**
 * 浏览器转发模块配置。
 * 对应配置文件中的 `browserForwarder` 项。
 *
 * @see <a href="https://www.v2fly.org/config/browserforwarder.html">浏览器转发文档</a>
 */
data class BrowserForwarder(
    /**
     * 浏览器转发页面的本地监听地址。
     */
    val listenAddr: String = "127.0.0.1",
    /**
     * 浏览器转发页面的本地监听端口。
     */
    val listenPort: Int = 8080,
)
