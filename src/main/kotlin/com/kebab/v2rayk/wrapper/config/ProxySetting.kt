package com.kebab.v2rayk.wrapper.config

data class ProxySetting(
    /**
     * 当指定另一个出站协议的标识时，此出站协议发出的数据，将被转发至所指定的出站协议发出。
     */
    var tag: String?,
)
