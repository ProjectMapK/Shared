package com.mapk.core

import com.mapk.annotations.KConstructor
import com.mapk.annotations.KParameterFlatten
import io.mockk.spyk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("パラメータのフラット化テスト")
class KParameterFlattenTest {
    data class InnerDst1(val quxQux: Int)
    data class InnerDst2(val quuxQuux: Int)
    data class InnerDst3(val graultGrault: String) {
        @KConstructor
        constructor(graultGrault: Int) : this(graultGrault.toString())
    }

    data class Dst(
        val fooFoo: Int,
        val barBar: Int,
        @KParameterFlatten
        val bazBaz: InnerDst1,
        @KParameterFlatten(fieldNameToPrefix = false)
        val corgeCorge: InnerDst2,
        @KParameterFlatten(nameJoiner = NameJoiner.Kebab::class)
        val garplyGarply: InnerDst3
    )

    companion object {
        val expectedParams: Set<String> =
            linkedSetOf("fooFoo", "barBar", "bazBazQuxQux", "quuxQuux", "garplyGarply-graultGrault")
        val expected: Dst = Dst(0, 1, InnerDst1(2), InnerDst2(3), InnerDst3("4"))
    }

    @Test
    fun test() {
        val spiedFunction = spyk(::Dst)
        val function = KFunctionForCall(spiedFunction, { it })

        function.requiredParameters.forEach {
            assertTrue(expectedParams.contains(it.name))
        }

        val adaptor = function.getArgumentAdaptor()
        expectedParams.forEachIndexed { i, str ->
            adaptor.putIfAbsent(str, i)
        }
        assertTrue(adaptor.isFullInitialized())

        val actual = function.call(adaptor)
        assertEquals(expected, actual)
        verify(exactly = 1) { spiedFunction.call(*anyVararg()) }
    }
}