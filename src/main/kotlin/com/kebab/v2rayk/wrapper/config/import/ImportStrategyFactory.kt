package com.kebab.v2rayk.wrapper.config.import

/**
 * 导入策略工厂：根据 URL scheme 匹配对应的 ImportStrategy。
 * 新协议只需添加策略实现并注册到此工厂。
 */
object ImportStrategyFactory {
    private val strategies: Map<String, ImportStrategy> = listOf(
        VmessStrategy(),
        ShadowsocksStrategy(),
    ).flatMap { strategy ->
        strategy.supportedSchemes.map { scheme -> scheme to strategy }
    }.toMap()

    /**
     * 根据 URL 的 scheme 返回对应的策略。
     * @throws UnsupportedSchemeException 无匹配策略时抛出
     */
    fun create(url: String): ImportStrategy {
        val scheme = url.substringBefore("://")
        return strategies[scheme.lowercase()]
            ?: throw UnsupportedSchemeException(scheme)
    }
}
