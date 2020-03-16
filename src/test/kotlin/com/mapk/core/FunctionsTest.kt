package com.mapk.core

import com.mapk.annotations.KParameterAlias
import io.mockk.every
import io.mockk.mockk
import kotlin.reflect.KParameter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

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
}
