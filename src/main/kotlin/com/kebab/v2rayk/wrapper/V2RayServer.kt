package com.kebab.v2rayk.wrapper

import com.kebab.v2rayk.wrapper.config.V2rayProperties

interface V2RayServer {

    /**
     * 启动 V2Ray 服务
     */
    fun start(config: V2rayProperties)

    /**
     * 启动 V2Ray 服务
     */
    fun start(configPath: String)

    /**
     * 停止 V2Ray 服务
     */
    fun stop()

    /**
     * 重启 V2Ray 服务
     */
    fun restart()

    /**
     * 获取 V2Ray 服务状态
     */
    fun status(): String

    /**
     * 获取 V2Ray 服务日志
     */
    fun getLogs(): String
}