import com.kebab.v2rayk.build.V2rayDownloadTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "com.kebab.v2rayk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("net.lingala.zip4j:zip4j:2.11.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.8.10")
}

// ==================== V2Ray 自动下载 ====================

val downloadV2ray by tasks.registering(V2rayDownloadTask::class) {
    targetDir.set(layout.projectDirectory.dir("tmp/vcore"))
    version.set(providers.gradleProperty("v2ray.version").orElse("latest"))
}

val setupV2rayTestConfig by tasks.registering {
    group = "v2ray"
    description = "Generates minimal test config.json for V2Ray tests"
    dependsOn(downloadV2ray)
    doLast {
        val configDir = layout.projectDirectory.dir("tmp").asFile
        configDir.mkdirs()
        val configFile = configDir.resolve("config.json")
        if (!configFile.exists()) {
            configFile.writeText("""
{
  "log": { "loglevel": "warning" },
  "inbounds": [
    { "port": 10808, "listen": "127.0.0.1", "protocol": "socks",
      "settings": { "auth": "noauth", "udp": true } }
  ],
  "outbounds": [
    { "protocol": "freedom", "tag": "direct",
      "settings": { "domainStrategy": "AsIs" } }
  ]
}
            """.trimIndent())
            logger.lifecycle("[v2ray-download] 已生成测试配置: ${configFile.absolutePath}")
        }
    }
}

tasks.test {
    useJUnitPlatform()
    dependsOn(downloadV2ray, setupV2rayTestConfig)
    systemProperty("v2ray.executable", "tmp/vcore/v2ray")
}

// 跳过下载：gradle -Pv2ray.skipDownload test
if (project.hasProperty("v2ray.skipDownload")) {
    downloadV2ray.configure { enabled = false }
    setupV2rayTestConfig.configure { enabled = false }
}

tasks.processTestResources

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

application {
    mainClass.set("MainKt")
}
