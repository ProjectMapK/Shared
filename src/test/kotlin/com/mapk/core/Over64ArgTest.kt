package com.mapk.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("65以上の引数の関数を呼び出すテスト")
class Over64ArgTest {
    data class Dst(
        val arg0: Int,
        val arg1: Int,
        val arg2: Int,
        val arg3: Int,
        val arg4: Int,
        val arg5: Int,
        val arg6: Int,
        val arg7: Int,
        val arg8: Int,
        val arg9: Int,
        val arg10: Int,
        val arg11: Int,
        val arg12: Int,
        val arg13: Int,
        val arg14: Int,
        val arg15: Int,
        val arg16: Int,
        val arg17: Int,
        val arg18: Int,
        val arg19: Int,
        val arg20: Int,
        val arg21: Int,
        val arg22: Int,
        val arg23: Int,
        val arg24: Int,
        val arg25: Int,
        val arg26: Int,
        val arg27: Int,
        val arg28: Int,
        val arg29: Int,
        val arg30: Int,
        val arg31: Int,
        val arg32: Int,
        val arg33: Int,
        val arg34: Int,
        val arg35: Int,
        val arg36: Int,
        val arg37: Int,
        val arg38: Int,
        val arg39: Int,
        val arg40: Int,
        val arg41: Int,
        val arg42: Int,
        val arg43: Int,
        val arg44: Int,
        val arg45: Int,
        val arg46: Int,
        val arg47: Int,
        val arg48: Int,
        val arg49: Int,
        val arg50: Int,
        val arg51: Int,
        val arg52: Int,
        val arg53: Int,
        val arg54: Int,
        val arg55: Int,
        val arg56: Int,
        val arg57: Int,
        val arg58: Int,
        val arg59: Int,
        val arg60: Int,
        val arg61: Int,
        val arg62: Int,
        val arg63: Int,
        val arg64: Int,
        val arg65: Int
    ) {
        companion object {
            val expected: Dst = Dst(
                0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
                10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
                20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
                30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
                40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
                50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
                60, 61, 62, 63, 64, 65
            )
        }
    }

    @Test
    fun test() {
        val functionForCall = KFunctionForCall(::Dst, { it })
        val dst: Dst = functionForCall.getArgumentAdaptor().apply {
            functionForCall.parameters.forEach {
                putIfAbsent(it.name!!, it.index)
            }
        }.let { functionForCall.call(it) }

        assertEquals(Dst.expected, dst)
    }
}
