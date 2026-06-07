package com.kebab.v2rayk.wrapper.config

/**
 * 目前统计信息没有任何参数，只要StatsObject项存在，内部的统计即会开启。
 * 同时你还需要在 Policy 中开启对应的项，才可以统计对应的数据。
 *
 * 目前已有的统计信息如下：
 *  用户数据
 *   特定用户的上行流量，单位字节。
 *    user>>>[email]>>>traffic>>>uplink
 *   特定用户的下行流量，单位字节。
 *    user>>>[email]>>>traffic>>>downlink
 *   如果对应用户没有指定 Email，则不会开启统计。
 *
 *  全局数据
 *   特定入站代理的上行流量，单位字节。
 *    inbound>>>[tag]>>>traffic>>>uplink
 *   特定入站代理的下行流量，单位字节。
 *    inbound>>>[tag]>>>traffic>>>downlink
 */
data class Stats(
    val unit: Unit? = null,
)
