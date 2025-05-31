package com.kebab.v2rayk.wrapper.process

import java.nio.file.Path

class V2RayCli(
    /**
     * v2rayCli可执行文件地址
     */
    private val v2rayCliPath: Path,
    /**
     * 只输出当前版本然后退出，不运行 V2Ray 主程序。
     */
    private val version: Boolean = false,

    /**
     * 测试配置文件有效性，如果有问题则输出错误信息，不运行 V2Ray 主程序。
     */
    private val test: Boolean = false,

    /**
     * 配置文件路径，可选的形式如下:
     *
     * 本地路径，可以是一个绝对路径，或者相对路径。
     * "stdin:": 表示将从标准输入读取配置文件内容，调用者必须在输入完毕后关闭标准输入流。
     * 以http://或https://(均为小写)开头: V2Ray 将尝试从这个远程地址加载配置文件。
     */
    val config: String? = null,

) {
    companion object {
        const val VERSION_OPTION = "version"
        const val TEST_OPTION = "test"
        const val RUN_OPTION = "run"
        const val CONFIG_OPTION = "-c"
        const val FORMAT_OPTION = "format"
        const val CONFIG_STDIN = "stdin:"
    }


    fun toProcessBuilder(): ProcessBuilder {
        return ArrayList<String>().apply {
            add(v2rayCliPath.toString())
            if (version) {
                add(VERSION_OPTION)
            }
            if (test) {
                add(TEST_OPTION)
            }
            if (config != null) {
                addAll(listOf(RUN_OPTION, CONFIG_OPTION, config))
            }
        }.let {
            ProcessBuilder(it)
        }
    }
}

enum class FormatType(val typeName: String) {
    JSON("json"),
    PROTOBUF("pb"),
}