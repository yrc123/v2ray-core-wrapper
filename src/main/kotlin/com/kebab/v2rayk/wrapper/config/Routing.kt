package com.kebab.v2rayk.wrapper.config

import com.fasterxml.jackson.annotation.JsonValue

/**
 * 预定义域名列表
 * 此列表由 domain-list-community 项目维护，预置于每一个 V2Ray 的安装包中，文件名为geosite.dat。
 *
 * 这个文件包含了一些常见的域名，可用于路由和 DNS 筛选。常用的域名有：
 *
 * category-ads: 包含了常见的广告域名。
 * category-ads-all: 包含了常见的广告域名，以及广告提供商的域名。
 * cn: 相当于 geolocation-cn 和 tld-cn 的合集。
 * google: 包含了 Google 旗下的所有域名。
 * facebook: 包含了 Facebook 旗下的所有域名。
 * geolocation-cn: 包含了常见的国内站点的域名。
 * geolocation-!cn: 包含了常见的非国内站点的域名。
 * speedtest: 包含了所有 Speedtest 所用的域名。
 * tld-cn: 包含了所有 .cn 和 .中国 结尾的域名。
 */
data class Routing(
	/**
	 * 域名解析策略，根据不同的设置使用不同的策略。
	 *
	 * "AsIs": 只使用域名进行路由选择。默认值。
	 * "IPIfNonMatch": 当域名没有匹配任何规则时，将域名解析成 IP（A 记录或 AAAA 记录）再次进行匹配；
	 * 		当一个域名有多个 A 记录时，会尝试匹配所有的 A 记录，直到其中一个与某个规则匹配为止；
	 * 		解析后的 IP 仅在路由选择时起作用，转发的数据包中依然使用原始域名；
	 * "IPOnDemand": 当匹配时碰到任何基于 IP 的规则，将域名立即解析为 IP 进行匹配；
	 */
	val domainStrategy: DomainStrategyType,
	/**
	 * 对应一个数组，数组中每个元素是一个规则。对于每一个连接，路由将根据这些规则依次进行判断，当一个规则生效时，即将这个连接转发至它所指定的outboundTag(或balancerTag，V2Ray 4.4+)。当没有匹配到任何规则时，流量默认由主出站协议发出。
	 */
	val rules: List<Rule> = mutableListOf(),
	/**
	 * (V2Ray 4.4+)一个数组，数组中每个元素是一个负载均衡器的配置。当一个规则指向一个负载均衡器时，V2Ray 会通过此负载均衡器选出一个出站协议，然后由它转发流量。
	 */
	val balancers: List<Balancer> = mutableListOf()
)

data class Rule(
	/**
	 * 目前只支持"field"这一个选项。
	 */
	val type: String = "field",
	/**
	 * 一个数组，数组每一项是一个域名的匹配。有以下几种形式：
	 *
	 * 纯字符串: 当此字符串匹配目标域名中任意部分，该规则生效。比如"sina.com"可以匹配"sina.com"、"sina.com.cn"和"www.sina.com"，但不匹配"sina.cn"。
	 * 正则表达式: 由"regexp:"开始，余下部分是一个正则表达式。当此正则表达式匹配目标域名时，该规则生效。例如"regexp:\\.goo.*\\.com$"匹配"www.google.com"、"fonts.googleapis.com"，但不匹配"google.com"。
	 * 子域名 (推荐): 由"domain:"开始，余下部分是一个域名。当此域名是目标域名或其子域名时，该规则生效。例如"domain:v2ray.com"匹配"www.v2ray.com"、"v2ray.com"，但不匹配"xv2ray.com"。
	 * 完整匹配: 由"full:"开始，余下部分是一个域名。当此域名完整匹配目标域名时，该规则生效。例如"full:v2ray.com"匹配"v2ray.com"但不匹配"www.v2ray.com"。
	 * 预定义域名列表：由"geosite:"开头，余下部分是一个名称，如geosite:google或者geosite:cn。名称及域名列表参考预定义域名列表。
	 * 从文件中加载域名: 形如"ext:file:tag"，必须以ext:（小写）开头，后面跟文件名和标签，文件存放在资源目录中，文件格式与geosite.dat相同，标签必须在文件中存在。
	 */
	val domain: List<String> = mutableListOf(),
	/**
	 * 一个数组，数组内每一个元素代表一个 IP 范围。当某一元素匹配目标 IP 时，此规则生效。有以下几种形式：
	 *
	 * IP: 形如"127.0.0.1"。
	 * CIDR: 形如"10.0.0.0/8".
	 * GeoIP: 形如"geoip:cn"，必须以geoip:（小写）开头，后面跟双字符国家代码，支持几乎所有可以上网的国家。
	 * 特殊值："geoip:private" (V2Ray 3.5+)，包含所有私有地址，如127.0.0.1。
	 * 从文件中加载 IP: 形如"ext:file:tag"，必须以ext:（小写）开头，后面跟文件名和标签，文件存放在资源目录中，文件格式与geoip.dat相同标签必须在文件中存在。
	 */
	val ip: List<String> = mutableListOf(),
	/**
	 * 端口范围，有三种形式：
	 *
	 * "a-b": a 和 b 均为正整数，且小于 65536。这个范围是一个前后闭合区间，当目标端口落在此范围内时，此规则生效。
	 * a: a 为正整数，且小于 65536。当目标端口为 a 时，此规则生效。
	 * (V2Ray 4.18+) 以上两种形式的混合，以逗号","分隔。形如："53,443,1000-2000"。
	 */
	val port: String,
	/**
	 * 可选的值有"tcp"、"udp"或"tcp,udp"，当连接方式是指定的方式时，此规则生效。
	 */
	val network: NetworkType,
	/**
	 * 一个数组，数组内每一个元素是一个 IP 或 CIDR。当某一元素匹配来源 IP 时，此规则生效。
	 */
	val source: List<String> = mutableListOf(),
	/**
	 * 一个数组，数组内每一个元素是一个邮箱地址。当某一元素匹配来源用户时，此规则生效。当前 Shadowsocks 和 VMess 支持此规则。
	 */
	val user: List<String> = mutableListOf(),
	/**
	 * 一个数组，数组内每一个元素是一个标识。当某一元素匹配入站协议的标识时，此规则生效。
	 */
	val inboundTag: List<String> = mutableListOf(),
	/**
	 * 一个数组，数组内每一个元素表示一种协议。当某一个协议匹配当前连接的流量时，此规则生效。必须开启入站代理中的sniffing选项。
	 */
	val protocol: List<ProtocolType> = mutableListOf(),
	/**
	 * (V2Ray 4.18+) 一段脚本，用于检测流量的属性值。当此脚本返回真值时，此规则生效。
	 * 脚本语言为 Starlark，它的语法是 Python 的子集。脚本接受一个全局变量attrs，其中包含了流量相关的属性。
	 * 目前只有 http 入站代理会设置这一属性。
	 * 示例：
	 * 		检测 HTTP GET: "attrs[':method'] == 'GET'"
	 * 		检测 HTTP Path: "attrs[':path'].startswith('/test')"
	 * 		检测 Content Type: "attrs['accept'].index('text/html') >= 0"
	 */
	val attrs: String? = null,
	/**
	 * 对应一个额外出站连接配置的标识。
	 */
	val outboundTag: String? = null,
	/**
	 * 对应一个负载均衡器的标识。balancerTag和outboundTag须二选一。当同时指定时，outboundTag生效。
	 */
	val balancerTag: String? = null
)

data class Balancer(
	/**
	 * 此负载均衡器的标识，用于匹配RuleObject中的balancerTag。
	 */
	val tag: String? = null,
	/**
	 * 一个字符串数组，其中每一个字符串将用于和出站协议标识的前缀匹配。在以下几个出站协议标识中：[ "a", "ab", "c", "ba" ]，"selector": ["a"]将匹配到[ "a", "ab" ]。
	 * 如果匹配到多个出站协议，负载均衡器目前会从中随机选出一个作为最终的出站协议。
	 */
	val selector: List<String> = mutableListOf()
)

enum class DomainStrategyType(@JsonValue val typeName: String) {
	ASIS("AsIs"),
	IPIF_NON_MATCH("IPIfNonMatch"),
	IPON_DEMAND("IPOnDemand"),
}
enum class NetworkType(@JsonValue val typeName: String) {
	TCP("tcp"),
	UDP("udp"),
	TCP_AND_UDP("tcp,udp"),
}
enum class ProtocolType(@JsonValue val typeName: String) {
	HTTP("http"),
	TLS("tls"),
	BITTORRENT("bittorrent")
}