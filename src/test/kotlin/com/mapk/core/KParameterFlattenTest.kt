package com.mapk.core

import com.mapk.annotations.KConstructor
import com.mapk.annotations.KParameterFlatten
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("パラメータのフラット化テスト")
class KParameterFlattenTest {
    data class InnerDst1(val quxQux: Int, @KParameterFlatten(fieldNameToPrefix = false) val fredFred: InnerInnerDst)
    data class InnerInnerDst(val waldoWaldo: Int)
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
            linkedSetOf("fooFoo", "barBar", "bazBazQuxQux", "waldoWaldo", "quuxQuux", "garplyGarply-graultGrault")
        val expected: Dst = Dst(
            0,
            1,
            InnerDst1(2, InnerInnerDst(3)),
            InnerDst2(4),
            InnerDst3("5")
        )
    }

    @Test
    fun test() {
        val function = KFunctionForCall(::Dst, { it })

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
    }
}
