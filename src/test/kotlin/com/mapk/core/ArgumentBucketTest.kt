package com.mapk.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private fun singleArgFunction(argument: Any?) {
    println(argument)
}

@DisplayName("ArgumentBucketTestのテスト")
class ArgumentBucketTest {
    @Nested
    @DisplayName("初期化状態のチェックテスト")
    inner class IsInitializedTest {
        private lateinit var argumentBucket: ArgumentBucket

        @BeforeEach
        fun beforeEach() {
            argumentBucket = KFunctionForCall(::singleArgFunction).getArgumentBucket()
        }

        @Test
        @DisplayName("初期化前")
        fun isNotInitialized() {
            assertFalse(argumentBucket.isInitialized)
        }

        @Test
        @DisplayName("初期化後")
        fun isInitialized() {
            argumentBucket.setArgument(object {}, 0)
            assertTrue(argumentBucket.isInitialized)
        }
    }
}
