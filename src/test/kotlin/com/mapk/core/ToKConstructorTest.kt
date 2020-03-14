package com.mapk.core

import com.mapk.annotations.KConstructor
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@Suppress("UNCHECKED_CAST", "unused")
@DisplayName("クラスからのコンストラクタ抽出関連テスト")
class ToKConstructorTest {
    private class SecondaryConstructorDst(val argument: Int) {
        @KConstructor
        constructor(argument: Number) : this(argument.toInt())
    }

    class CompanionFactoryDst(val argument: IntArray) {
        companion object {
            @KConstructor
            fun factory(csv: String): CompanionFactoryDst {
                return csv.split(",").map { it.toInt() }.toIntArray().let { CompanionFactoryDst(it) }
            }
        }
    }
    private class ConstructorDst(val argument: String)
    class MultipleConstructorDst @KConstructor constructor(val argument: Int) {
        @KConstructor
        constructor(argument: String) : this(argument.toInt())
    }

    private fun <T : Any> KFunctionForCall<T>.getTargetFunction(): KFunction<T> {
        return this::class.memberProperties.first { it.name == "function" }.getter.let {
            it.isAccessible = true
            it.call(this) as KFunction<T>
        }
    }

    @Test
    @DisplayName("セカンダリコンストラクタからの取得テスト")
    fun testGetFromSecondaryConstructor() {
        val function = SecondaryConstructorDst::class.toKConstructor().function
        Assertions.assertTrue(function.annotations.any { it is KConstructor })
    }

    @Test
    @DisplayName("ファクトリーメソッドからの取得テスト")
    fun testGetFromFactoryMethod() {
        val function = CompanionFactoryDst::class.toKConstructor().function
        Assertions.assertTrue(function.annotations.any { it is KConstructor })
    }

    @Test
    @DisplayName("無指定でプライマリコンストラクタからの取得テスト")
    fun testGetFromPrimaryConstructor() {
        val function = ConstructorDst::class.toKConstructor().function
        Assertions.assertEquals(ConstructorDst::class.primaryConstructor, function)
    }

    @Test
    @DisplayName("対象を複数指定した場合のテスト")
    fun testMultipleDeclareConstructor() {
        assertThrows<IllegalArgumentException> { MultipleConstructorDst::class.toKConstructor() }
    }
}
