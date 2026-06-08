package com.kebab.v2rayk.wrapper.process

import com.kebab.v2rayk.wrapper.V2RayCliServer
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.io.path.Path

class V2rayCliTest {

    // ==================== toString 命令拼接测试 ====================

    private val v2rayExe = Path("./v2ray.exe")

    @Test
    fun `toString - Version command`() {
        val cli = V2RayCli(v2rayCliPath = v2rayExe, command = V2RayCli.Command.Version)
        Assertions.assertEquals("$v2rayExe version", cli.toString())
    }

    @Test
    fun `toString - Run command with config`() {
        val cli = V2RayCli(v2rayExe, V2RayCli.Command.Run(config = "config.json"))
        Assertions.assertEquals("$v2rayExe run -c config.json", cli.toString())
    }

    @Test
    fun `toString - Run command with stdin`() {
        val cli = V2RayCli(v2rayExe, V2RayCli.Command.Run(config = "stdin:"))
        Assertions.assertEquals("$v2rayExe run -c stdin:", cli.toString())
    }

    @Test
    fun `toString - Run command with all options`() {
        val cli = V2RayCli(
            v2rayCliPath = v2rayExe,
            command = V2RayCli.Command.Run(
                config = "config.json",
                confdir = "/etc/v2ray",
                recursive = true,
                format = "json",
            ),
        )
        Assertions.assertEquals(
            "$v2rayExe run -c config.json -d /etc/v2ray -r -format json",
            cli.toString(),
        )
    }

    @Test
    fun `toString - Run command minimal`() {
        val cli = V2RayCli(v2rayExe, V2RayCli.Command.Run())
        Assertions.assertEquals("$v2rayExe run", cli.toString())
    }

    @Test
    fun `toString - Test command`() {
        val cli = V2RayCli(v2rayExe, V2RayCli.Command.Test(config = "config.json"))
        Assertions.assertEquals("$v2rayExe test -c config.json", cli.toString())
    }

    // ==================== 集成测试（需要 v2ray 可执行文件） ====================

    @Test
    fun testV2rayVersion() {
        val process = V2RayCli(
            v2rayCliPath = Path("./tmp/vcore/v2ray.exe"),
            command = V2RayCli.Command.Version,
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
        v2RayCliServer.stop()
    }
}
