package com.mapk.core.internal

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions

@DisplayName("完全初期化時呼び出しのテスト")
class FullInitializedFunctionWrapperTest {
    data class Dst(val foo: Int, val bar: String) {
        companion object {
            fun of(foo: Int, bar: String) = Dst(foo, bar)
        }
    }
    fun instanceMethod(foo: Int, bar: String) = Dst(foo, bar)
    private val expected = Dst(1, "2")

    @Test
    @DisplayName("コンストラクタの場合")
    fun constructorTest() {
        val fullInitializedFunctionWrapper = FullInitializedFunctionWrapper(::Dst, null, 2)
        assertEquals(expected, fullInitializedFunctionWrapper.call(arrayOf(1, "2")))
    }

    @Test
    @DisplayName("コンパニオンオブジェクトに定義した関数の場合")
    fun companionObjectFunTest() {
        val func = Dst::class.companionObject!!.functions.first { it.name == "of" }
        val instance = Dst::class.companionObjectInstance!!
        val fullInitializedFunctionWrapper = FullInitializedFunctionWrapper(
            func,
            instance,
            3
        )
        assertEquals(expected, fullInitializedFunctionWrapper.call(arrayOf(instance, 1, "2")))
    }

    @Nested
    @DisplayName("その他の場合")
    inner class OthersTest {
        @Test
        @DisplayName("コンパニオンオブジェクトに定義した関数をメソッドリファレンスで取得した場合")
        fun companionObjectFunByMethodReferenceTest() {
            val fullInitializedFunctionWrapper = FullInitializedFunctionWrapper((Dst)::of, null, 2)
            assertEquals(expected, fullInitializedFunctionWrapper.call(arrayOf(1, "2")))
        }

        @Test
        @DisplayName("インスタンスメソッドの場合")
        fun instanceMethodTest() {
            val fullInitializedFunctionWrapper = FullInitializedFunctionWrapper(::instanceMethod, null, 2)
            assertEquals(expected, fullInitializedFunctionWrapper.call(arrayOf(1, "2")))
        }
    }
}
