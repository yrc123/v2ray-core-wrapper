package com.kebab.v2rayk.wrapper.config.import

import com.kebab.v2rayk.wrapper.config.V2rayProperties

/**
 * URL 导入策略接口。
 * 每种协议（vmess、ss 等）实现自己的解析逻辑。
 *
 * 输入：单个 URL 字符串（已 trim()）
 * 输出：V2rayProperties（含一个 Outbound，tag = "PROXY"）
 */
interface ImportStrategy {
    /** 支持的 URL scheme 列表（不含 ://），如 ["vmess"] */
    val supportedSchemes: List<String>

    /**
     * 解析 URL 为 V2rayProperties。
     * @param url 完整的分享链接，如 vmess://... 或 ss://...
     * @throws ImportException 解析失败时抛出
     */
    fun parse(url: String): V2rayProperties
}
