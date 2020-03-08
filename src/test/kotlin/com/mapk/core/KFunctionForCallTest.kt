package com.mapk.core

import kotlin.reflect.full.functions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class KFunctionForCallTest {
    @Nested
    @DisplayName("初期化関連テスト")
    inner class InitializeTest {
        // 空引数の関数
        private fun dummy1() {}
        @Suppress("UNUSED_PARAMETER")
        private fun dummy2(input: Int) {}

        @Test
        @DisplayName("不正な関数を入力した場合")
        fun withNoInstance() {
            assertThrows<IllegalArgumentException> { KFunctionForCall(this::dummy1) }
        }

        @Test
        @DisplayName("不正な関数を入力した場合（インスタンス付き = ファクトリーメソッド想定）")
        fun withInstance() {
            assertThrows<IllegalArgumentException> { KFunctionForCall(this::dummy2, object {}) }
        }

        @Test
        @DisplayName("正常入力")
        fun isValid() {
            assertDoesNotThrow { KFunctionForCall(this::dummy2) }
        }
    }

    @Nested
    @DisplayName("呼び出し関連テスト")
    inner class CallTest {
        @Test
        @DisplayName("コンパニオンオブジェクトから取得した場合")
        fun fromCompanionObject() {
            val function =
                Companion::class.functions.find { it.name == (KFunctionForCallTest)::declaredOnCompanionObject.name }!!

            val kFunctionForCall = KFunctionForCall(function, Companion)

            val bucket = kFunctionForCall.getArgumentBucket()
            kFunctionForCall.parameters.forEach { bucket.setArgument(it, it.index) }
            val result = kFunctionForCall.call(bucket)
            assertEquals("12", result)
        }

        private fun func(key: String, value: String = "default"): Pair<String, String> = key to value

        @Test
        @DisplayName("デフォルト値を用いる場合")
        fun useDefaultValue() {
            val kFunctionForCall = KFunctionForCall(::func)
            val argumentBucket = kFunctionForCall.getArgumentBucket()

            ::func.parameters.forEach { if (!it.isOptional) argumentBucket.setArgument(it, it.name) }

            val result = kFunctionForCall.call(argumentBucket)
            assertEquals("key" to "default", result)
        }
    }

    companion object {
        fun declaredOnCompanionObject(arg1: Any, arg2: Any): String {
            return arg1.toString() + arg2.toString()
        }
    }
}
