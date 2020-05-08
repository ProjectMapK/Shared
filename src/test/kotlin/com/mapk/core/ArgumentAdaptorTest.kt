package com.mapk.core

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
        }
    }
}
