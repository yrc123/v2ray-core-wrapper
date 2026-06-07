package com.kebab.v2rayk.wrapper.config

import com.fasterxml.jackson.annotation.JsonValue

data class Log(
	/**
	 * 访问日志的文件地址，其值是一个合法的文件地址
	 * 如"/tmp/v2ray/_access.log"（Linux）或者"C:\\Temp\\v2ray\\_access.log"（Windows）。
	 * 当此项不指定或为空值时，表示将日志输出至 stdout。
	 * V2Ray 4.20 加入了特殊值none，即关闭access log。
	 */
	var access: String = "",
	/**
	 * 错误日志的文件地址，其值是一个合法的文件地址
	 * 如"/tmp/v2ray/_error.log"（Linux）或者"C:\\Temp\\v2ray\\_error.log"（Windows）。
	 * 当此项不指定或为空值时，表示将日志输出至 stdout。V2Ray 4.20 加入了特殊值none，即关闭error log（跟loglevel: "none"等价）。
	 */
	var error: String = "",
	/**
	 * 错误日志的级别。默认值为"warning"。
	 *
	 * "debug": 只有开发人员能看懂的信息。同时包含所有"info"内容。
	 * "info": V2Ray 在运行时的状态，不影响正常使用。同时包含所有"warning"内容。
	 * "warning": V2Ray 遇到了一些问题，通常是外部问题，不影响 V2Ray 的正常运行，但有可能影响用户的体验。同时包含所有"error"内容。
	 * "error": V2Ray 遇到了无法正常运行的问题，需要立即解决。
	 * "none": 不记录任何内容。
	 */
	var loglevel: LogLevel = LogLevel.INFO,
)

enum class LogLevel(@JsonValue val logLevel: String) {
	DEBUG("debug"),
	INFO("info"),
	WARNING("warning"),
	ERROR("error"),
	NONE("none"),
}

