package com.mapk.core.internal

import com.mapk.annotations.KParameterAlias
import com.mapk.annotations.KUseDefaultArgument
import io.mockk.every
import io.mockk.mockk
import java.lang.IllegalArgumentException
import kotlin.reflect.KParameter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("関数類のテスト")
class FunctionsTest {
    lateinit var parameter: KParameter

    @BeforeEach
    fun beforeEach() {
        parameter = mockk()
    }

    @Nested
    @DisplayName("エイリアス関連のテスト")
    inner class GetAliasOrNameTest {
        @Test
        @DisplayName("エイリアス無し")
        fun noAlias() {
            val paramName = "mocked name"
            every { parameter.annotations } returns emptyList()
            every { parameter.name } returns paramName

            assertEquals(paramName, parameter.getAliasOrName())
        }

        @Test
        @DisplayName("エイリアス有り")
        fun withAlias() {
            val aliasedName = "aliased name"

            val alias = mockk<KParameterAlias>()
            every { alias.value } returns aliasedName

            every { parameter.annotations } returns listOf(alias)

            assertEquals(aliasedName, parameter.getAliasOrName())
        }
    }

    @Nested
    @DisplayName("デフォルト引数を用いるかの判定関数のテスト")
    inner class IsUseDefaultArgumentTest {
        @Test
        @DisplayName("デフォルト引数を用いない場合")
        fun noDefaultArgument() {
            every { parameter.annotations } returns emptyList()
            assertFalse(parameter.isUseDefaultArgument())
        }

        @Test
        @DisplayName("デフォルト引数を用いる指定が有るが、実際はデフォルト引数が設定されていない場合")
        fun isIncorrect() {
            every { parameter.annotations } returns listOf(mockk<KUseDefaultArgument>())
            every { parameter.isOptional } returns false
            assertThrows<IllegalArgumentException> { parameter.isUseDefaultArgument() }
        }

        @Test
        @DisplayName("正常入力")
        fun isCorrect() {
            every { parameter.annotations } returns listOf(mockk<KUseDefaultArgument>())
            every { parameter.isOptional } returns true
            assertTrue(parameter.isUseDefaultArgument())
        }
    }
}
