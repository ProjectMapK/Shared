package com.mapk.core

import com.mapk.annotations.KParameterRequireNonNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

private fun sampleFunction(arg1: Any?, arg2: Any?, arg3: Any?) {
    println(arg1)
    println(arg2)
    println(arg3)
}

private fun sampleAnnotatedFunction(@KParameterRequireNonNull arg1: Any, arg2: Any?) {
    println(arg1)
    println(arg2)
}

@DisplayName("ArgumentBucketTestのテスト")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ArgumentBucketTest {
    @Nested
    @DisplayName("シンプルな呼び出しのテスト")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class SimpleTest {
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
                assertEquals("argument", argumentBucket[parameter])
            }

            @Test
            @DisplayName("同じインデックスに2回追加した場合")
            fun setArgumentTwice() {
                val parameter = ::sampleFunction.parameters.first { it.index == 0 }

                argumentBucket.putIfAbsent(parameter, "first")
                argumentBucket.putIfAbsent(parameter, "second")
                assertEquals("first", argumentBucket[parameter])
            }
        }
    }

    @Nested
    @DisplayName("アノテーションを付与した場合のテスト")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class AnnotatedParametersTest {
        @Test
        @DisplayName("non-null要求のテスト")
        fun isRequireNonNull() {
            val forCall = KFunctionForCall(::sampleAnnotatedFunction)
            val argumentBucket = forCall.getArgumentBucket()
            val parameters = forCall.parameters

            argumentBucket.putIfAbsent(parameters[0], null)
            assertFalse(argumentBucket.containsKey(parameters[0]))

            argumentBucket.putIfAbsent(parameters[0], "input")
            assertTrue(argumentBucket.containsKey(parameters[0]))
            assertEquals("input", argumentBucket[parameters[0]])

            argumentBucket.putIfAbsent(parameters[1], null)
            assertTrue(argumentBucket.containsKey(parameters[1]))
            assertNull(argumentBucket[parameters[1]])
        }
    }
}
