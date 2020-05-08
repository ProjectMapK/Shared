package com.mapk.core

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("ArgumentAdaptorのテスト")
class ArgumentAdaptorTest {
    private val keys = listOf("foo", "bar", "baz")
    private val adaptor: ArgumentAdaptor = keys.mapIndexed { i, argName ->
        mockk<ValueParameter<*>> {
            every { name } returns argName
            every { isNullable } returns (i % 2 == 0)
        }
    }.associateBy {
        it.name
    }.let { ArgumentAdaptor(it) }

    @Nested
    @DisplayName("値をバインドするテスト")
    inner class PutIfAbsentTest {
        @Nested
        @DisplayName("Null入力")
        inner class NullabilityTest {
            @Test
            @DisplayName("nullableかつnull入力")
            fun isNullableAndNull() {
                adaptor.putIfAbsent(keys[0], null)
                assertTrue(adaptor.isInitialized(keys[0]))
            }

            @Test
            @DisplayName("non-nullかつnull入力")
            fun isNonNullAndNull() {
                adaptor.putIfAbsent(keys[1], null)
                assertFalse(adaptor.isInitialized(keys[0]))
            }

            @Test
            @DisplayName("2重バインドテスト")
            fun isDuplicateKey() {
                adaptor.putIfAbsent(keys[0], keys[0])
                adaptor.putIfAbsent(keys[0], keys[1])
                assertEquals(keys[0], adaptor.readout(keys[0]))
            }
        }
    }

    @Nested
    @DisplayName("完全初期化チェックのテスト")
    inner class IsFullInitializedTest {
        @Test
        @DisplayName("完全初期化していない場合")
        fun isNotFullInitialized() {
            adaptor.putIfAbsent(keys[0], keys[0])
            adaptor.putIfAbsent(keys[1], keys[1])
            assertFalse(adaptor.isFullInitialized())
        }

        @Test
        @DisplayName("完全初期化した場合")
        fun isFullInitialized() {
            keys.forEach { adaptor.putIfAbsent(it, it) }
            assertTrue(adaptor.isFullInitialized())
        }
    }

    @Nested
    @DisplayName("存在しないkeyの読み出しテスト")
    inner class NotContainsKeyTest {
        @Test
        @DisplayName("isInitializedのテスト")
        fun isInitialized() {
            assertThrows<IllegalArgumentException> { adaptor.isInitialized("hoge") }
        }

        @Test
        @DisplayName("putIfAbsentのテスト")
        fun putIfAbsent() {
            assertThrows<IllegalArgumentException> { adaptor.putIfAbsent("hoge", "hoge") }
        }
    }

    @Test
    @DisplayName("読み出しテスト")
    fun readoutTest() {
        keys.forEach { adaptor.putIfAbsent(it, it) }
        keys.forEach { assertEquals(it, adaptor.readout(it)) }
    }
}
