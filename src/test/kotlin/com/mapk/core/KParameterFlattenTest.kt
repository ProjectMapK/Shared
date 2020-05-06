package com.mapk.core

import com.mapk.annotations.KParameterFlatten
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("パラメータのフラット化テスト")
class KParameterFlattenTest {
    data class InnerDst1(val quxQux: Int)
    data class InnerDst2(val quuxQuux: Int)

    data class Dst(
        val fooFoo: Int,
        val barBar: Int,
        @KParameterFlatten
        val bazBaz: InnerDst1,
        @KParameterFlatten(fieldNameToPrefix = false)
        val corgeCorge: InnerDst2
    )

    companion object {
        val expectedParams: Set<String> = linkedSetOf("fooFoo", "barBar", "bazBazQuxQux", "quuxQuux")
        val expected: Dst = Dst(0, 1, InnerDst1(2), InnerDst2(3))
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
