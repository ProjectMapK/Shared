package com.mapk.core

import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KParameter
import kotlin.reflect.full.functions

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KFunctionForCallTest {
    @Nested
    @DisplayName("初期化関連テスト")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class InitializeTest {
        // 空引数の関数
        private fun dummy1() {}
        @Suppress("UNUSED_PARAMETER")
        private fun dummy2(input: Int) {}

        @Test
        @DisplayName("不正な関数を入力した場合")
        fun withNoInstance() {
            assertThrows<IllegalArgumentException> { KFunctionForCall(this::dummy1, { it }) }
        }

        @Test
        @DisplayName("不正な関数を入力した場合（インスタンス付き = ファクトリーメソッド想定）")
        fun withInstance() {
            assertThrows<IllegalArgumentException> { KFunctionForCall(this::dummy2, { it }, object {}) }
        }

        @Test
        @DisplayName("正常入力")
        fun isValid() {
            assertDoesNotThrow { KFunctionForCall(this::dummy2, { it }) }
        }
    }

    @Nested
    @DisplayName("呼び出し関連テスト")
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class CallTest {
        @Test
        @DisplayName("コンパニオンオブジェクトから取得した場合")
        fun fromCompanionObject() {
            val function = Companion::class.functions
                .first { it.name == (KFunctionForCallTest)::declaredOnCompanionObject.name }
                .let { spyk(it) }

            val kFunctionForCall = KFunctionForCall(function, { it }, Companion)

            val adaptor = kFunctionForCall.getArgumentAdaptor()
            adaptor.putIfAbsent("arg1", 1)
            adaptor.putIfAbsent("arg2", 2)
            val result = kFunctionForCall.call(adaptor)
            assertEquals("12", result)
            verify(exactly = 1) { function.call(*anyVararg()) }
        }

        private fun func(key: String, value: String = "default"): Pair<String, String> = key to value

        @Test
        @DisplayName("デフォルト値を用いる場合")
        fun useDefaultValue() {
            val func = spyk(::func)
            val kFunctionForCall = KFunctionForCall(func, { it })
            val adaptor = kFunctionForCall.getArgumentAdaptor()

            func.parameters.forEach { if (!it.isOptional) adaptor.putIfAbsent(it.name!!, it.name) }

            val result = kFunctionForCall.call(adaptor)
            assertEquals("key" to "default", result)
            verify(exactly = 1) { func.callBy(any()) }
        }

        @Test
        @DisplayName("同一関数を違うソースから複数回呼んだ場合")
        fun multipleCall() {
            val function = Companion::class.functions
                .first { it.name == (KFunctionForCallTest)::declaredOnCompanionObject.name }
                .let { spyk(it) }

            val kFunctionForCall = KFunctionForCall(function, { it }, Companion)

            val adaptor1 = kFunctionForCall.getArgumentAdaptor()
            kFunctionForCall.parameters
                .filter { it.kind == KParameter.Kind.VALUE }
                .forEach { adaptor1.putIfAbsent(it.name!!, it.index) }
            val result1 = kFunctionForCall.call(adaptor1)
            assertEquals("12", result1)

            val adaptor2 = kFunctionForCall.getArgumentAdaptor()
            kFunctionForCall.parameters
                .filter { it.kind == KParameter.Kind.VALUE }
                .forEach { adaptor2.putIfAbsent(it.name!!, it.index + 1) }
            val result2 = kFunctionForCall.call(adaptor2)
            assertEquals("23", result2)

            verify(exactly = 2) { function.call(*anyVararg()) }
        }
    }

    companion object {
        fun declaredOnCompanionObject(arg1: Any, arg2: Any): String {
            return arg1.toString() + arg2.toString()
        }
    }
}
