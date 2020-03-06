package com.mapk.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertIterableEquals
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
            argumentBucket.setArgument(object {}, 0)
            argumentBucket.setArgument(object {}, 1)
            argumentBucket.setArgument(object {}, 2)
            assertTrue(argumentBucket.isInitialized)
        }
    }

    @Nested
    @DisplayName("初期化されていないインデックス取得のテスト")
    inner class NotInitializedParameterIndexesTest {
        @Test
        @DisplayName("何もセットしていない場合")
        fun noArguments() {
            assertIterableEquals(listOf(0, 1, 2), argumentBucket.notInitializedParameterIndexes)
        }

        @Test
        @DisplayName("1つセットした場合")
        fun singleArgument() {
            argumentBucket.setArgument(object {}, 1)
            assertIterableEquals(listOf(0, 2), argumentBucket.notInitializedParameterIndexes)
        }

        @Test
        @DisplayName("全てセットした場合")
        fun fullArguments() {
            argumentBucket.setArgument(object {}, 0)
            argumentBucket.setArgument(object {}, 1)
            argumentBucket.setArgument(object {}, 2)
            assertIterableEquals(emptyList<Any?>(), argumentBucket.notInitializedParameterIndexes)
        }
    }

    @Nested
    @DisplayName("引数セットのテスト")
    inner class SetArgumentTest {
        @Test
        @DisplayName("正常に追加した場合")
        fun setNewArgument() {
            argumentBucket.setArgument("argument", 0)
            assertEquals("argument", argumentBucket.bucket[0])
        }

        @Test
        @DisplayName("同じインデックスに2回追加した場合")
        fun setArgumentTwice() {
            argumentBucket.setArgument("first", 0)
            argumentBucket.setArgument("second", 0)
            assertEquals("first", argumentBucket.bucket[0])
        }
    }
}
