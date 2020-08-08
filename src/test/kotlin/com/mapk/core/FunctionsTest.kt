package com.mapk.core

import com.mapk.annotations.KConstructor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.reflect.full.companionObjectInstance

@DisplayName("共通利用関数関連のテスト")
class FunctionsTest {
    class NoCompanionObject

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
    annotation class TestAnnotation1

    @Target(AnnotationTarget.FUNCTION)
    @Retention(AnnotationRetention.RUNTIME)
    @MustBeDocumented
    annotation class TestAnnotation2

    class WithCompanionObject {
        companion object {
            @KConstructor
            fun kConstructor() {}

            @TestAnnotation2
            fun testAnnotation1() {}

            @TestAnnotation2
            fun testAnnotation2() {}
        }
    }

    @Nested
    @DisplayName("コンパニオンオブジェクトから指定したアノテーションを取得するテスト")
    inner class GetAnnotatedFunctionsFromCompanionObject {
        @Test
        @DisplayName("コンパニオンオブジェクトが無い場合")
        fun noCompanionObject() {
            assertNull(NoCompanionObject::class.getAnnotatedFunctionsFromCompanionObject<KConstructor>())
        }

        @Test
        @DisplayName("コンパニオンオブジェクトが有るが関数が取れない場合")
        fun withCompanionObjectButNotFound() {
            assertNull(WithCompanionObject::class.getAnnotatedFunctionsFromCompanionObject<TestAnnotation1>())
        }

        @Test
        @DisplayName("単体取得")
        fun kConstructor() {
            val result =
                WithCompanionObject::class.getAnnotatedFunctionsFromCompanionObject<KConstructor>()!!

            assertEquals(WithCompanionObject::class.companionObjectInstance!!, result.first)
            assertEquals(1, result.second.size)
            assertEquals("kConstructor", result.second.single().name)
        }

        @Test
        @DisplayName("複数取得")
        fun testAnnotation() {
            val result =
                WithCompanionObject::class.getAnnotatedFunctionsFromCompanionObject<TestAnnotation2>()!!

            assertEquals(WithCompanionObject::class.companionObjectInstance!!, result.first)
            assertEquals(2, result.second.size)
            val names = listOf("testAnnotation1", "testAnnotation2")
            assertTrue(names.contains(result.second[0].name))
            assertTrue(names.contains(result.second[1].name))
        }
    }

    data class InnerClass(val arg: Int)
    data class DataClass(val innerClass: InnerClass)

    @Test
    @DisplayName("パラメータからの型取得テスト")
    fun getKClassTest() {
        val param = ::DataClass.parameters.single()
        assertEquals(InnerClass::class, param.getKClass())
    }
}
