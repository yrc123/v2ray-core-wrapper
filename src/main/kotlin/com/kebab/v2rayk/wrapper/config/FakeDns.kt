package com.kebab.v2rayk.wrapper.config

/**
 * FakeDNS 虚拟 DNS 服务器配置。
 * 对应配置文件中的 `fakedns` 项。
 *
 * @see <a href="https://www.v2fly.org/config/fakedns.html">FakeDNS 文档</a>
 */
data class FakeDns(
    /**
     * FakeDNS 分配 IP 的地址空间（CIDR），如 "198.18.0.0/15"。
     */
    val ipPool: String,
    /**
     * FakeDNS 所记忆的「IP - 域名映射」数量。
     */
    val poolSize: Int,
)
