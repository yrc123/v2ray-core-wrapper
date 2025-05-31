package com.kebab.v2rayk.wrapper.process

import com.kebab.v2rayk.wrapper.V2RayCliServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class V2rayCliTest {
    @Test
    fun testV2rayVersion() {
        val process = V2RayCli(
            v2rayCliPath = Path("./tmp/vcore/v2ray.exe"),
            version = true,
        ).toProcessBuilder()
            .start()
        val v2rayVersionInfo = process.inputReader().readText()
        println(v2rayVersionInfo)
        Assertions.assertTrue(v2rayVersionInfo.contains("V2Ray"))
    }
    @Test
    fun testV2rayTest() {
        val v2RayCliServer = V2RayCliServer(Path("./tmp/vcore/v2ray.exe"))
        v2RayCliServer.start("./tmp/config.json")
        Thread.sleep(10000)
        v2RayCliServer.stop()
    }
}