package com.mapk.core

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
}
