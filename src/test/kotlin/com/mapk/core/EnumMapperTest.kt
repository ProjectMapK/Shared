package com.mapk.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

@DisplayName("EnumMapperのテスト")
class EnumMapperTest {
    enum class JvmLanguage {
        Java, Scala, Groovy, Kotlin
    }

    @ParameterizedTest
    @EnumSource(value = JvmLanguage::class)
    @DisplayName("正常系")
    fun test(value: JvmLanguage) {
        assertEquals(value, EnumMapper.getEnum(JvmLanguage::class.java, value.name))
    }

    @Test
    @DisplayName("null/空文字列入力")
    fun isNull() {
        assertNull(EnumMapper.getEnum(JvmLanguage::class.java, null))
        assertNull(EnumMapper.getEnum(JvmLanguage::class.java, ""))
    }

    @Test
    @DisplayName("存在しないもの")
    fun isNotExisting() {
        assertThrows<IllegalArgumentException> { EnumMapper.getEnum(JvmLanguage::class.java, "C") }
    }
}
