package com.kebab.v2rayk.wrapper

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kebab.v2rayk.wrapper.config.V2rayProperties
import com.kebab.v2rayk.wrapper.process.V2RayCli
import java.nio.file.Path

/**
 * V2Ray命令行服务器实现。
 *
 * @param v2rayCliPath V2Ray可执行文件路径
 */
class V2RayCliServer(
    private val v2rayCliPath: Path,
) : V2RayServer {

    private var process: Process? = null
    private var lastConfigPath: String? = null

    init {
        require(v2rayCliPath.toString().isNotBlank()) { "V2Ray CLI path cannot be empty" }
    }

    /**
     * 将 [V2rayProperties] 序列化为 JSON，通过 stdin 管道传给 V2Ray 进程。
     * 使用 "stdin:" 配置路径，V2Ray 将从标准输入读取配置内容。
     */
    override fun start(config: V2rayProperties) {
        if (process != null && process!!.isAlive) {
            throw IllegalStateException("V2Ray server is already running")
        }

        val json = jacksonObjectMapper()
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(config)

        process = V2RayCli(
            v2rayCliPath = v2rayCliPath,
            command = V2RayCli.Command.Run(config = "stdin:"),
        ).toProcessBuilder()
            .start()

        // 同步写入 JSON 到进程 stdin，然后关闭流触发 V2Ray 读取配置
        try {
            process!!.outputStream.use { out ->
                out.write(json.toByteArray())
                out.flush()
            }
        } catch (e: Exception) {
            // 写入失败时确保进程被清理
            process?.destroy()
            process = null
            throw RuntimeException("Failed to write config to V2Ray stdin", e)
        }

        val v2rayVersionInfo = process!!.inputReader().readText()
        println(v2rayVersionInfo)
    }

    /**
     * 使用指定的配置文件路径启动 V2Ray。
     */
    override fun start(configPath: String) {
        if (process != null && process!!.isAlive) {
            throw IllegalStateException("V2Ray server is already running")
        }
        lastConfigPath = configPath
        process = V2RayCli(
            v2rayCliPath = v2rayCliPath,
            command = V2RayCli.Command.Run(config = configPath),
        ).toProcessBuilder()
            .start()
        val v2rayVersionInfo = process!!.inputReader().readText()
        println(v2rayVersionInfo)
    }

    override fun stop() {
        process?.destroy()
        process = null
    }

    /**
     * 重启 V2Ray：先停止，再使用上次的配置文件路径重新启动。
     */
    override fun restart() {
        val configPath = lastConfigPath
            ?: throw IllegalStateException("No previous configuration to restart from")
        stop()
        start(configPath)
    }

    /**
     * 获取 V2Ray 进程状态。
     * @return "Running" 或 "Stopped"
     */
    override fun status(): String {
        return if (process?.isAlive == true) "Running" else "Stopped"
    }

    /**
     * 获取 V2Ray 进程的标准输出日志。
     * 注意：当前实现仅返回进程的 inputReader 内容（启动时的版本信息）。
     * 要获取完整的运行日志，需在配置中指定 Log.access/Log.error 路径后读取对应文件。
     */
    override fun getLogs(): String {
        return process?.inputReader()?.readText() ?: "No process running"
    }
}
