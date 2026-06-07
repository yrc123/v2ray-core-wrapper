package com.kebab.v2rayk.wrapper.config.bound.settings

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.kebab.v2rayk.wrapper.config.bound.ProtocolType

/**
 * 入站协议的 Settings 多态基类。
 * 各协议子类由 [InboundDeserializer] 根据父级 [ProtocolType] 选择反序列化。
 */
sealed class InboundSettings
