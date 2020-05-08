package com.mapk.core.internal

import com.mapk.core.NameJoiner
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("パラメータ名変換関連のテスト")
class ParameterNameConverterTest {
    @Nested
    @DisplayName("シンプルな変換機能のテスト")
    inner class SimpleTest {
        private val simple = ParameterNameConverter.Simple { it.toLowerCase() }

        @Test
        @DisplayName("単純な変換テスト")
        fun convertTest() {
            val expected = "abcdef"
            val actual = simple.convert("AbCdEf")
            assertEquals(expected, actual)
        }

        @Test
        @DisplayName("ネストしたインスタンスを作るテスト")
        fun nestTest() {
            val nested1 = simple.nest("AbCdEf", NameJoiner.Kebab)
            run {
                val expected = "abcdef-ghijkl"
                val actual = nested1.convert("gHiJkL")
                assertEquals(expected, actual)
            }

            val nested2 = nested1.nest("gHiJkL", NameJoiner.Snake)
            run {
                val expected = "abcdef-ghijkl_mnopqr"
                val actual = nested2.convert("MNopQR")
                assertEquals(expected, actual)
            }
        }

        @Test
        @DisplayName("Simpleにした場合のテスト")
        fun simple() {
            val simple2 = simple.toSimple()
            assertEquals(simple, simple2)
        }
    }

    @Nested
    @DisplayName("プレフィックス付き変換機能のテスト")
    inner class WithPrefixTest {
        private val withPrefix =
            ParameterNameConverter.WithPrefix("abcdef", NameJoiner.Snake) { it.toUpperCase() }

        @Test
        @DisplayName("単純な変換テスト")
        fun convertTest() {
            val expected = "ABCDEF_GHIJKL"
            val actual = withPrefix.convert("GhIJkL")
            assertEquals(expected, actual)
        }

        @Test
        @DisplayName("ネストしたインスタンスを作るテスト")
        fun nestTest() {
            val nested = withPrefix.nest("GhIJkL", NameJoiner.Kebab)
            run {
                val expected = "ABCDEF_GHIJKL-MNOPQR"
                val actual = nested.convert("mnOpQr")
                assertEquals(expected, actual)
            }
        }

        @Test
        @DisplayName("Simpleにした場合のテスト")
        fun simple() {
            val simple = withPrefix.toSimple()
            val expected = "ABCDEF"
            val actual = simple.convert(expected)
            assertEquals(expected, actual)
        }
    }
}
