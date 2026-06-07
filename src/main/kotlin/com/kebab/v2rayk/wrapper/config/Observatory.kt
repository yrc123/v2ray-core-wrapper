package com.kebab.v2rayk.wrapper.config

/**
 * 连接观测模块配置。
 * 对应配置文件中的 `observatory` 项。
 *
 * @see <a href="https://www.v2fly.org/config/observatory.html">Observatory 文档</a>
 */
data class Observatory(
    /**
     * 一个字符串数组，用于和出站协议标识的前缀匹配。
     * 被匹配到的出站连接将被定时探测以确定是否可用。
     */
    val subjectSelector: List<String>,
    /**
     * 用于检测连接状态的 URL。
     */
    val probeURL: String? = null,
    /**
     * 发起探测的间隔，如 "10s", "2h45m"。
     */
    val probeInterval: String? = null,
)
