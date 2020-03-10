package com.mapk.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private fun sampleFunction(arg1: Any?, arg2: Any?, arg3: Any?) {
    println(arg1)
    println(arg2)
    println(arg3)
}

@DisplayName("ArgumentBucketTestのテスト")
class ArgumentBucketTest {
    private lateinit var argumentBucket: ArgumentBucket

    @BeforeEach
    fun beforeEach() {
        argumentBucket = KFunctionForCall(::sampleFunction).getArgumentBucket()
    }

    @Nested
    @DisplayName("初期化状態のチェックテスト")
    inner class IsInitializedTest {
        @Test
        @DisplayName("初期化前")
        fun isNotInitialized() {
            assertFalse(argumentBucket.isInitialized)
        }

        @Test
        @DisplayName("初期化後")
        fun isInitialized() {
            ::sampleFunction.parameters.forEach {
                argumentBucket.putIfAbsent(it, object {})
            }

            assertTrue(argumentBucket.isInitialized)
        }
    }

    @Nested
    @DisplayName("引数セットのテスト")
    inner class SetArgumentTest {
        @Test
        @DisplayName("正常に追加した場合")
        fun setNewArgument() {
            val parameter = ::sampleFunction.parameters.first { it.index == 0 }
            argumentBucket.putIfAbsent(parameter, "argument")
            assertEquals("argument", argumentBucket.getByIndex(0))
        }

        @Test
        @DisplayName("同じインデックスに2回追加した場合")
        fun setArgumentTwice() {
            val parameter = ::sampleFunction.parameters.first { it.index == 0 }

            argumentBucket.putIfAbsent(parameter, "first")
            argumentBucket.putIfAbsent(parameter, "second")
            assertEquals("first", argumentBucket.getByIndex(0))
        }
    }
}
