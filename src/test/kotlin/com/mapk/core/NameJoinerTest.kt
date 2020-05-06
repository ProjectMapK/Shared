package com.mapk.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@DisplayName("NameJoinerの名前結合処理が正しいかのテスト")
class NameJoinerTest {
    companion object {
        const val prefix = "prefix"
        const val suffix = "suffix"
    }

    @Nested
    @DisplayName("Camel")
    inner class CamelTest {
        private val camel = NameJoiner.Camel

        @Test
        @DisplayName("prefixが空文字")
        fun emptyPrefix() {
            assertEquals(suffix, camel.join("", suffix))
        }

        @Test
        @DisplayName("suffixが空文字列")
        fun emptySuffix() {
            assertEquals(prefix, camel.join(prefix, ""))
        }

        @Test
        @DisplayName("suffixが1文字")
        fun singleSuffix() {
            assertEquals("${prefix}A", camel.join(prefix, "a"))
        }

        @Test
        @DisplayName("通常結合")
        fun normal() {
            assertEquals("${prefix}Suffix", camel.join(prefix, suffix))
        }
    }

    @Nested
    @DisplayName("Snake")
    inner class SnakeTest {
        private val snake = NameJoiner.Snake

        @Test
        @DisplayName("prefixが空文字")
        fun emptyPrefix() {
            assertEquals(suffix, snake.join("", suffix))
        }

        @Test
        @DisplayName("suffixが空文字列")
        fun emptySuffix() {
            assertEquals(prefix, snake.join(prefix, ""))
        }

        @Test
        @DisplayName("suffixが1文字")
        fun singleSuffix() {
            assertEquals("${prefix}_a", snake.join(prefix, "a"))
        }

        @Test
        @DisplayName("通常結合")
        fun normal() {
            assertEquals("${prefix}_$suffix", snake.join(prefix, suffix))
        }
    }

    @Nested
    @DisplayName("Kebab")
    inner class KebabTest {
        private val kebab = NameJoiner.Kebab

        @Test
        @DisplayName("prefixが空文字")
        fun emptyPrefix() {
            assertEquals(suffix, kebab.join("", suffix))
        }

        @Test
        @DisplayName("suffixが空文字列")
        fun emptySuffix() {
            assertEquals(prefix, kebab.join(prefix, ""))
        }

        @Test
        @DisplayName("suffixが1文字")
        fun singleSuffix() {
            assertEquals("$prefix-a", kebab.join(prefix, "a"))
        }

        @Test
        @DisplayName("通常結合")
        fun normal() {
            assertEquals("$prefix-$suffix", kebab.join(prefix, suffix))
        }
    }
}
