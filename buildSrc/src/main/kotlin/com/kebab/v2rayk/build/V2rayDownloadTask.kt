package com.kebab.v2rayk.build

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream

/**
 * 自动下载 V2Ray Core 可执行文件的 Gradle Task。
 *
 * 从 GitHub Releases 拉取匹配当前平台的 V2Ray 压缩包，解压到目标目录。
 * 已存在则跳过。
 */
abstract class V2rayDownloadTask : DefaultTask() {

    @get:Internal
    abstract val targetDir: DirectoryProperty

    @get:Input
    @get:Optional
    abstract val version: Property<String>

    init {
        group = "v2ray"
        description = "Downloads V2Ray core binary for the current platform from GitHub Releases"
    }

    @TaskAction
    fun download() {
        val dir = targetDir.asFile.get()
        dir.mkdirs()

        val exeName = if (isWindows) "v2ray.exe" else "v2ray"
        val v2rayExe = File(dir, exeName)

        if (v2rayExe.exists()) {
            logger.lifecycle("[v2ray-download] V2Ray 已存在: ${v2rayExe.absolutePath}，跳过下载")
            return
        }

        // 1. GitHub API
        val apiUrl = resolveApiUrl()
        logger.lifecycle("[v2ray-download] 查询: $apiUrl")
        val releaseJson = fetchJson(apiUrl)
        val tag = releaseJson.asJsonObject["tag_name"].asString

        // 2. 匹配平台
        val asset = findMatchingAsset(releaseJson)
        if (asset == null) {
            throw GradleException(
                "[v2ray-download] 未找到匹配 ${platformLabel()} 的发行包。" +
                    "请访问 https://github.com/v2fly/v2ray-core/releases 手动下载到 ${dir.absolutePath}"
            )
        }

        val name = asset.asJsonObject["name"].asString
        val url = asset.asJsonObject["browser_download_url"].asString
        logger.lifecycle("[v2ray-download] $tag → $name")

        // 3. 下载 & 解压
        val zipFile = File(dir, "v2ray-tmp-${System.currentTimeMillis()}.zip")
        try {
            downloadFile(url, zipFile)
            logger.lifecycle("[v2ray-download] 解压中...")
            extractZip(zipFile, dir)
            if (!isWindows) v2rayExe.setExecutable(true)
            logger.lifecycle("[v2ray-download] ✅ $tag 就绪: ${v2rayExe.absolutePath}")
        } finally {
            zipFile.delete()
        }
    }

    // ==================== 平台 ====================

    private val isWindows get() = osName.contains("win")
    private val isMac get() = osName.contains("mac") || osName.contains("darwin")
    private val isArm get() = System.getProperty("os.arch").lowercase()
        .let { it.contains("aarch64") || it.contains("arm64") }
    private val osName get() = System.getProperty("os.name").lowercase()

    private fun platformLabel(): String {
        val os = when {
            isWindows -> "windows"
            isMac -> "macos"
            else -> "linux"
        }
        return "$os-${if (isArm) "arm64" else "x64"}"
    }

    // ==================== GitHub API ====================

    private fun resolveApiUrl(): String {
        val ver = version.orNull
        return if (ver.isNullOrBlank() || ver == "latest") {
            "https://api.github.com/repos/v2fly/v2ray-core/releases/latest"
        } else {
            "https://api.github.com/repos/v2fly/v2ray-core/releases/tags/$ver"
        }
    }

    private fun fetchJson(urlString: String): JsonElement {
        val conn = httpGet(urlString, "application/json")
        try {
            check(conn.responseCode == 200) {
                val body = runCatching { conn.errorStream?.bufferedReader()?.readText() ?: "" }.getOrDefault("")
                "[v2ray-download] GitHub API ${conn.responseCode}: ${body.take(300)}"
            }
            return JsonParser.parseString(conn.inputStream.bufferedReader().readText())
        } finally {
            conn.disconnect()
        }
    }

    private fun downloadFile(urlString: String, dest: File) {
        val conn = httpGet(urlString, "application/octet-stream")
        try {
            check(conn.responseCode in 200..399) {
                "[v2ray-download] 下载失败 HTTP ${conn.responseCode}"
            }
            val total = conn.contentLengthLong
            val input = conn.inputStream
            val output = FileOutputStream(dest)
            try {
                copyWithProgress(input, output, total)
            } finally {
                output.close()
                input.close()
            }
        } finally {
            conn.disconnect()
        }
    }

    private fun copyWithProgress(
        input: java.io.InputStream,
        output: FileOutputStream,
        total: Long,
    ) {
        val buf = ByteArray(8192)
        var downloaded = 0L
        var nextReport = if (total > 0) total / 10 else Long.MAX_VALUE
        while (true) {
            val n = input.read(buf)
            if (n == -1) break
            output.write(buf, 0, n)
            downloaded += n
            if (downloaded >= nextReport) {
                val pct = if (total > 0) downloaded * 100 / total else 0
                val msg = buildString {
                    append("[v2ray-download]   ${pct}% (${formatSize(downloaded)}")
                    if (total > 0) append(" / ${formatSize(total)}")
                    append(")")
                }
                logger.lifecycle(msg)
                nextReport += if (total > 0) total / 10 else Long.MAX_VALUE
            }
        }
    }

    private fun httpGet(urlString: String, accept: String): HttpURLConnection {
        val conn = URL(urlString).openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000
        conn.readTimeout = 120_000
        conn.setRequestProperty("Accept", accept)
        val token = System.getenv("GITHUB_TOKEN") ?: System.getenv("GH_TOKEN")
        if (token != null) conn.setRequestProperty("Authorization", "Bearer $token")
        conn.connect()
        return conn
    }

    // ==================== 匹配平台资源 ====================

    private fun findMatchingAsset(release: JsonElement): JsonElement? {
        return release.asJsonObject["assets"].asJsonArray.firstOrNull { asset ->
            val name = asset.asJsonObject["name"].asString.lowercase()
            name.endsWith(".zip") && matchesPlatform(name)
        }
    }

    private fun matchesPlatform(name: String): Boolean {
        val matchesOs = when {
            isWindows -> name.contains("windows")
            isMac -> name.contains("macos") || name.contains("darwin")
            else -> name.contains("linux")
        }
        if (!matchesOs) return false
        return if (isArm) name.contains("arm") else !name.contains("arm")
    }

    // ==================== 解压 ====================

    private fun extractZip(zipFile: File, destDir: File) {
        val zis = ZipInputStream(zipFile.inputStream().buffered())
        try {
            var entry = zis.nextEntry
            while (entry != null) {
                val target = File(destDir, entry.name)
                if (entry.isDirectory) {
                    target.mkdirs()
                } else {
                    target.parentFile.mkdirs()
                    Files.copy(zis, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        } finally {
            zis.close()
        }
    }

    // ==================== 工具 ====================

    private fun formatSize(bytes: Long): String = when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
