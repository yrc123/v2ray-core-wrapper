package com.kebab.v2rayk.wrapper.config.import

/**
 * 导入 URL 解析异常基类。
 */
open class ImportException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * 无对应策略异常（不支持的 scheme）。
 */
class UnsupportedSchemeException(scheme: String) : ImportException("不支持的链接类型: $scheme")

/**
 * URL 格式非法异常（Base64、JSON 解析失败或必填字段缺失）。
 */
class InvalidUrlFormatException(message: String, cause: Throwable? = null) : ImportException(message, cause)
