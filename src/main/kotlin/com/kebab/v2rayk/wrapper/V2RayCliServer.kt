package com.kebab.v2rayk.wrapper

import com.kebab.v2rayk.wrapper.config.V2rayProperties
import com.kebab.v2rayk.wrapper.process.V2RayCli
import java.nio.file.Path

/**
 * V2Ray命令行服务器实现
 *
 * @param v2rayCli V2Ray命令行接口
 * @param v2rayCliPath V2Ray可执行文件路径
 * @param configPath 配置文件路径
 */
class V2RayCliServer(
    private val v2rayCliPath: Path,
) : V2RayServer {

    private var process: Process? = null

    init {
        require(v2rayCliPath.toString().isNotBlank()) { "V2Ray CLI path cannot be empty" }
    }

    override fun start(config: V2rayProperties) {
        TODO("Not yet implemented")
    }

    override fun start(configPath: String) {
        if (process != null && process!!.isAlive) {
            throw IllegalStateException("V2Ray server is already running")
        }
        process = V2RayCli(
            v2rayCliPath = v2rayCliPath,
            config = configPath
        ).toProcessBuilder()
            .start()
        val v2rayVersionInfo = process!!.inputReader().readText()
        println(v2rayVersionInfo)
    }

    override fun stop() {
        process?.destroy()
        process = null
    }

    override fun restart() {
        stop()
        // Assuming we have a method to get the current configuration
        // start(getCurrentConfiguration())
        TODO("Not yet implemented")
    }

    override fun status(): String {
        TODO("Not yet implemented")
    }

    override fun getLogs(): String {
        TODO("Not yet implemented")
    }
}