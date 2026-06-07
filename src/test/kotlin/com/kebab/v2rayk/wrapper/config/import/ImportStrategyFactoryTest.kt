package com.kebab.v2rayk.wrapper.config.import

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ImportStrategyFactoryTest {

    @Test
    fun `factory returns VmessStrategy for vmess URL`() {
        val url = "vmess://ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1-0@host:443/#Test"
        val strategy = ImportStrategyFactory.create(url)
        assertTrue(strategy is VmessStrategy)
    }

    @Test
    fun `factory returns ShadowsocksStrategy for ss URL`() {
        val url = "ss://YWVzLTI1Ni1nY206YnlrQ29ManN5Vw@host:39120#Test"
        val strategy = ImportStrategyFactory.create(url)
        assertTrue(strategy is ShadowsocksStrategy)
    }

    @Test
    fun `factory throws UnsupportedSchemeException for unknown scheme`() {
        assertFailsWith<UnsupportedSchemeException> {
            ImportStrategyFactory.create("trojan://host:443#Test")
        }
    }

    @Test
    fun `factory throws UnsupportedSchemeException for http URL`() {
        assertFailsWith<UnsupportedSchemeException> {
            ImportStrategyFactory.create("http://example.com")
        }
    }

    @Test
    fun `factory is case-insensitive for scheme`() {
        val url = "VMESS://ecdd5fb7-867f-4218-8eb6-7c5be4aa06e1-0@host:443/#Test"
        val strategy = ImportStrategyFactory.create(url)
        assertTrue(strategy is VmessStrategy)
    }
}
