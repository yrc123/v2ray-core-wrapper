package com.kebab.v2rayk.wrapper.process

import java.nio.file.Path
import kotlin.io.path.Path

class V2rayCoreCliWrapper(private val v2rayPath: Path, private val osType: OSType) {
    companion object {
        const val WINDOWS_CLI = "v2ray.exe";
        const val UNIX_CLI = "v2ray";
    }
    init {
        val cliExec = Path(v2rayPath.toString(), when(osType) {
            OSType.WINDOWS -> WINDOWS_CLI
            OSType.LINUX -> UNIX_CLI
            OSType.MAC -> UNIX_CLI
        })
//        if (!v2rayPath.exists()) {
//            throw RuntimeException("文件夹不存在")
//        }
//        if (!cliExec.exists()) {
//            throw RuntimeException("文件夹不存在")
//        }
    }

}
enum class OSType {
    WINDOWS(),
    LINUX(),
    MAC,
}