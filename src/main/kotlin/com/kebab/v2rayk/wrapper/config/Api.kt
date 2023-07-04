package com.kebab.v2rayk.wrapper.config

import com.fasterxml.jackson.annotation.JsonValue

data class Api(
	/**
	 * 出站代理标识
	 */
	var tag: String,
	/**
	 * 开启的 API 列表
	 */
	var services: List<ApiType> = mutableListOf(),
)
enum class ApiType(@JsonValue val apiName: String) {
	/**
	 * 些对于入站出站代理进行修改的 API，可用的功能如下：
	 *
	 * 添加一个新的入站代理；
	 * 添加一个新的出站代理；
	 * 删除一个现有的入站代理；
	 * 删除一个现有的出站代理；
	 * 在一个入站代理中添加一个用户（仅支持 VMess）；
	 * 在一个入站代理中删除一个用户（仅支持 VMess）；
	 */
	HANDLER_SERVICE("HandlerService"),
	/**
	 * 支持对内置 Logger 的重启，可配合 logrotate 进行一些对日志文件的操作。
	 */
	LOGGER_SERVICE("LoggerService"),
	/**
	 * 内置的数据统计服务，详见统计信息。
	 */
	STATS_SERVICE("StatsService"),
}

