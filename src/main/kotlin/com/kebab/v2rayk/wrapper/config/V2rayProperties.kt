package com.kebab.v2rayk.wrapper.config

data class V2rayProperties(
	val log: Log? = null,
	val api: Api? = null,
	val dns: Dns? = null,
	val stats: Stats? = null,
	val routing: Routing? = null,
	val policy: Policy? = null,
//	val reverse: Reverse? = null,
	val inbounds: List<Inbound>? = null,
//	val outbounds: List<Outbounds>? = null,
//	val transport: Transport? = null,
)
