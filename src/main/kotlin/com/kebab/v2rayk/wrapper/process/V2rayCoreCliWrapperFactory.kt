package com.kebab.v2rayk.wrapper.process

import org.apache.commons.lang3.SystemUtils
import java.lang.RuntimeException
import java.nio.file.Path
import kotlin.io.path.exists

object V2rayCoreCliWrapperFactory {
    fun createCurrentOSCli(v2rayPath: Path): V2rayCoreCliWrapper {
        return V2rayCoreCliWrapper(v2rayPath, getCurrentOS())
    }
    private fun getCurrentOS(): OSType{
        return if (SystemUtils.IS_OS_WINDOWS) {
            OSType.WINDOWS
        } else if (SystemUtils.IS_OS_LINUX) {
            OSType.LINUX
        } else if(SystemUtils.IS_OS_MAC) {
            OSType.MAC
        } else {
            throw UnsupportedOperationException("不支持的操作系统")
        }
    }
}