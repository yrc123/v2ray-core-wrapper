package com.kebab.v2rayk.wrapper.process

import java.nio.file.Path

/**
 * V2Ray CLI 命令行参数构建器。
 *
 * 新版 V2Ray CLI 使用子命令模式：
 * ```
 * v2ray <command> [arguments]
 * ```
 *
 * @param v2rayCliPath V2Ray 可执行文件路径
 * @param command 要执行的子命令
 */
class V2RayCli(
    private val v2rayCliPath: Path,
    private val command: Command,
) {
    /**
     * V2Ray CLI 子命令。
     */
    sealed class Command {
        /**
         * 运行 V2Ray。
         *
         * @param config 配置文件路径，支持以下形式：
         *   - 本地路径（绝对或相对）
         *   - "stdin:" 从标准输入读取配置
         *   - 以 http:// 或 https:// 开头的远程地址
         * @param confdir 配置文件目录
         * @param recursive 递归加载 confdir
         * @param format 配置文件格式（"auto", "json", "pb" 等，默认自动检测）
         */
        data class Run(
            val config: String? = null,
            val confdir: String? = null,
            val recursive: Boolean = false,
            val format: String? = null,
        ) : Command()

        /**
         * 测试配置文件有效性。
         */
        data class Test(
            val config: String,
        ) : Command()

        /**
         * 输出版本信息后退出。
         */
        object Version : Command()
    }

    companion object {
        const val CMD_RUN = "run"
        const val CMD_TEST = "test"
        const val CMD_VERSION = "version"
        const val OPT_CONFIG = "-c"
        const val OPT_CONFDIR = "-d"
        const val OPT_RECURSIVE = "-r"
        const val OPT_FORMAT = "-format"
    }

    /**
     * 构建命令行参数列表，由 [toProcessBuilder] 和 [toString] 复用。
     */
    private fun buildArgs(): List<String> {
        return buildList {
            add(v2rayCliPath.toString())
            when (command) {
                is Command.Run -> {
                    add(CMD_RUN)
                    if (command.config != null) {
                        addAll(listOf(OPT_CONFIG, command.config))
                    }
                    if (command.confdir != null) {
                        addAll(listOf(OPT_CONFDIR, command.confdir))
                    }
                    if (command.recursive) {
                        add(OPT_RECURSIVE)
                    }
                    if (command.format != null) {
                        addAll(listOf(OPT_FORMAT, command.format))
                    }
                }
                is Command.Test -> {
                    addAll(listOf(CMD_TEST, OPT_CONFIG, command.config))
                }
                is Command.Version -> {
                    add(CMD_VERSION)
                }
            }
        }
    }

    fun toProcessBuilder(): ProcessBuilder {
        return ProcessBuilder(buildArgs())
    }

    /**
     * 返回拼接后的命令行字符串，便于调试和日志输出。
     */
    override fun toString(): String {
        return buildArgs().joinToString(" ")
    }
}

/**
 * 配置文件格式枚举。
 */
enum class FormatType(val typeName: String) {
    JSON("json"),
    PROTOBUF("pb"),
}
