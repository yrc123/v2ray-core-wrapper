package com.kebab.v2rayk.wrapper.config.import

import com.kebab.v2rayk.wrapper.config.bound.ProtocolType
import com.kebab.v2rayk.wrapper.config.bound.settings.VmessOutboundSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class VmessStrategyTest {
    private val strategy = VmessStrategy()

    @Test
    fun `parse valid V2RayN format vmess URL`() {
        val url = "vmess://tcp:ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1-0@c7s3.portablesubmarines.com:39120/#JMS-143136@c7s3.portablesubmarines.com:39120"
        val result = strategy.parse(url)

        val outbounds = result.outbounds
        assertNotNull(outbounds)
        assertEquals(1, outbounds.size)

        val outbound = outbounds[0]
        assertEquals(ProtocolType.VMESS, outbound.protocol)
        assertEquals("JMS-143136", outbound.tag)
        assertEquals("0.0.0.0", outbound.sendThrough)

        val settings = outbound.settings as VmessOutboundSettings
        assertEquals(1, settings.vnext.size)
        assertEquals("c7s3.portablesubmarines.com", settings.vnext[0].address)
        assertEquals(39120, settings.vnext[0].port)
        assertEquals("ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1", settings.vnext[0].users[0].id)
        assertEquals(0, settings.vnext[0].users[0].alterId)
    }

    @Test
    fun `parse vmess URL without network prefix`() {
        val url = "vmess://ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1-0@c7s3.portablesubmarines.com:39120/#MyNode"
        val result = strategy.parse(url)

        val settings = result.outbounds!![0].settings as VmessOutboundSettings
        assertEquals("c7s3.portablesubmarines.com", settings.vnext[0].address)
        assertEquals(39120, settings.vnext[0].port)
        assertEquals("MyNode", result.outbounds!![0].tag)
    }

    @Test
    fun `parse vmess URL without fragment uses host_port as tag`() {
        val url = "vmess://ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1-0@1.2.3.4:8080/"
        val result = strategy.parse(url)
        assertEquals("1.2.3.4:8080", result.outbounds!![0].tag)
    }

    @Test
    fun `parse vmess URL uses fragment before at as tag`() {
        val url = "vmess://ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1-0@1.2.3.4:8080/#NodeName@1.2.3.4:8080"
        val result = strategy.parse(url)
        assertEquals("NodeName", result.outbounds!![0].tag)
    }

    @Test
    fun `parse vmess URL without aid`() {
        val url = "vmess://ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1@1.2.3.4:8080/#Test"
        val result = strategy.parse(url)
        val settings = result.outbounds!![0].settings as VmessOutboundSettings
        assertEquals(0, settings.vnext[0].users[0].alterId)
    }

    @Test
    fun `invalid vmess URL missing at sign throws InvalidUrlFormatException`() {
        assertFailsWith<InvalidUrlFormatException> {
            strategy.parse("vmess://this-is-not-a-valid-url")
        }
    }

    @Test
    fun `invalid vmess URL bad port throws InvalidUrlFormatException`() {
        assertFailsWith<InvalidUrlFormatException> {
            strategy.parse("vmess://ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1-0@host:abc/#Test")
        }
    }

    @Test
    fun `invalid vmess URL port out of range throws InvalidUrlFormatException`() {
        assertFailsWith<InvalidUrlFormatException> {
            strategy.parse("vmess://ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1-0@host:99999/#Test")
        }
    }
}
