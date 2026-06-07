package com.kebab.v2rayk.wrapper.config.import

import com.kebab.v2rayk.wrapper.config.bound.ProtocolType
import com.kebab.v2rayk.wrapper.config.bound.settings.ShadowsocksOutboundSettings
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class ShadowsocksStrategyTest {
    private val strategy = ShadowsocksStrategy()

    @Test
    fun `parse valid SIP002 SS URL`() {
        val url = "ss://YWVzLTI1Ni1nY206YnlrQ29ManN5Vw@c7s1.portablesubmarines.com:39120#JMS-143136@c7s1.portablesubmarines.com:39120"
        val result = strategy.parse(url)

        val outbounds = result.outbounds
        assertNotNull(outbounds)
        assertEquals(1, outbounds.size)

        val outbound = outbounds[0]
        assertEquals(ProtocolType.SHADOWSOCKS, outbound.protocol)
        assertEquals("JMS-143136@c7s1.portablesubmarines.com:39120", outbound.tag)

        val settings = outbound.settings as ShadowsocksOutboundSettings
        assertEquals(1, settings.servers.size)
        assertEquals("c7s1.portablesubmarines.com", settings.servers[0].address)
        assertEquals(39120, settings.servers[0].port)
        assertEquals("aes-256-gcm", settings.servers[0].method)
        assertEquals("bykCoLjsyW", settings.servers[0].password)
    }

    @Test
    fun `parse SS URL without fragment uses host_port as tag`() {
        val url = "ss://YWVzLTI1Ni1nY206YnlrQ29ManN5Vw@1.2.3.4:8080"
        val result = strategy.parse(url)
        assertEquals("1.2.3.4:8080", result.outbounds!![0].tag)
    }

    @Test
    fun `parse SS URL without port defaults to 8388`() {
        val url = "ss://YWVzLTI1Ni1nY206YnlrQ29ManN5Vw@1.2.3.4#MyNode"
        val result = strategy.parse(url)
        val settings = result.outbounds!![0].settings as ShadowsocksOutboundSettings
        assertEquals(8388, settings.servers[0].port)
    }

    @Test
    fun `parse SS URL with simple tag`() {
        val url = "ss://YWVzLTI1Ni1nY206cGFzc3dvcmQ@1.2.3.4:8388#MySimpleTag"
        val result = strategy.parse(url)
        assertEquals("MySimpleTag@1.2.3.4:8388", result.outbounds!![0].tag)
    }

    @Test
    fun `parse SS URL with another cipher`() {
        // base64 of "chacha20-ietf-poly1305:test@123"
        val url = "ss://Y2hhY2hhMjAtaWV0Zi1wb2x5MTMwNTp0ZXN0QDEyMw@1.2.3.4:8388#Test"
        val result = strategy.parse(url)
        val settings = result.outbounds!![0].settings as ShadowsocksOutboundSettings
        assertEquals("chacha20-ietf-poly1305", settings.servers[0].method)
        assertEquals("test@123", settings.servers[0].password)
    }

    @Test
    fun `invalid SS URL missing at sign throws InvalidUrlFormatException`() {
        assertFailsWith<InvalidUrlFormatException> {
            strategy.parse("ss://this-is-not-valid")
        }
    }

    @Test
    fun `invalid SS URL bad base64 throws InvalidUrlFormatException`() {
        assertFailsWith<InvalidUrlFormatException> {
            strategy.parse("ss://!!!not-base64!!!@host:123")
        }
    }

    @Test
    fun `invalid SS URL missing method_password separator throws InvalidUrlFormatException`() {
        // Base64 of "nocolon" (method without password)
        val url = "ss://bm9jb2xvbg@host:123"
        assertFailsWith<InvalidUrlFormatException> {
            strategy.parse(url)
        }
    }
}
