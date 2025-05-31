package com.kebab.v2rayk.wrapper.config

data class Dns(
	/**
	 * 静态 IP 列表，其值为一系列的"域名":"地址"。其中地址可以是 IP 或者域名。在解析域名时，如果域名匹配这个列表中的某一项，当该项的地址为 IP 时，则解析结果为该项的 IP，而不会使用下述的 servers 进行解析；当该项的地址为域名时，会使用此域名进行 IP 解析，而不使用原始域名。
	 *
	 * 域名的格式有以下几种形式：
	 *
	 * 纯字符串: 当此域名完整匹配目标域名时，该规则生效。例如"v2ray.com"匹配"v2ray.com"但不匹配"www.v2ray.com"。
	 * 正则表达式: 由"regexp:"开始，余下部分是一个正则表达式。当此正则表达式匹配目标域名时，该规则生效。例如"regexp:\\.goo.*\\.com$"匹配"www.google.com"、"fonts.googleapis.com"，但不匹配"google.com"。
	 * 子域名 (推荐): 由"domain:"开始，余下部分是一个域名。当此域名是目标域名或其子域名时，该规则生效。例如"domain:v2ray.com"匹配"www.v2ray.com"、"v2ray.com"，但不匹配"xv2ray.com"。
	 * 子串: 由"keyword:"开始，余下部分是一个字符串。当此字符串匹配目标域名中任意部分，该规则生效。比如"keyword:sina.com"可以匹配"sina.com"、"sina.com.cn"和"www.sina.com"，但不匹配"sina.cn"。
	 * 预定义域名列表：由"geosite:"开头，余下部分是一个名称，如geosite:google或者geosite:cn。名称及域名列表参考预定义域名列表。
	 */
	var hosts: Map<String, String> = mutableMapOf(),
	/**
	 * 一个 DNS 服务器列表，支持的类型有两种：DNS地址（字符串形式）和ServerObject 。
	 *
	 * 当它的值是一个 DNS IP 地址时，如"8.8.8.8"，V2Ray 会使用此地址的 53 端口进行 DNS 查询。
	 *
	 * 当值为"localhost"时，表示使用本机预设的 DNS 配置。
	 *
	 * 当值是"https://host:port/dns-query"的形式，如"https://dns.google/dns-query"，V2Ray 会使用DNS over HTTPS (RFC8484, 简称DOH) 进行查询。有些服务商拥有IP别名的证书，可以直接写IP形式，比如https://1.1.1.1/dns-query。也可使用非标准端口和路径，如"https://a.b.c.d:8443/my-dns-query" (4.22.0+)
	 *
	 * 当值是"https+local://host:port/dns-query"的形式，如"https+local://dns.google/dns-query"，V2Ray 会使用 DOH本地模式 进行查询，即DOH请求不会经过Routing/Outbound等组件，直接对外请求，以降低耗时。一般适合在服务端使用。也可使用非标端口和路径。(4.22.0+)
	 */
	var servers: List<ServersItem> = mutableListOf(),
	/**
	 * 当前系统的 IP 地址，用于 DNS 查询时，通知服务器客户端的所在位置。不能是私有地址。
	 */
	var clientIp: String? = null,
	/**
	 * (V2Ray 4.13+) 由此 DNS 发出的查询流量，除localhost 和 DOHL_ 模式外，都会带有此标识，可在路由使用inboundTag进行匹配。
	 */
	var tag: String? = null
)

data class ServersItem(
	/**
	 * DNS 服务器地址，如"8.8.8.8"。
	 * 对于普通DNS IP地址只支持 UDP 协议的 DNS 服务器，若地址是以"https://"或"https+local://"开头的URL形式，则使用DOH模式，规则同字符串模式的DOH配置。
	 */
	var address: String,
	/**
	 * DNS 服务器端口，如53。此项缺省时默认为53。当使用DOH模式该项无效，非标端口应在URL中指定。
	 */
	var port: Int,
	/**
	 * 一个域名列表，此列表包含的域名，将优先使用此服务器进行查询。域名格式和路由配置中相同。
	 */
	var domains: List<String> = mutableListOf(),
	/**
	 * (V2Ray 4.22.0+) 一个 IP 范围列表，格式和路由配置中相同。
	 * 当配置此项时，V2Ray DNS 会对返回的 IP 的进行校验，只返回包含 expectIPs 列表中的地址。
	 * 如果未配置此项，会原样返回 IP 地址。
	 */
	var expectIPs: List<String>? = null
)
