package com.xcodersteam.cpue.kcpu1

import junit.framework.TestCase.assertEquals
import org.junit.Test

/**
 * Created by Semoro on 03.10.16.
 * ©XCodersTeam, 2016
 */

class SimpleAsmTest : AbstractAsmTest() {

    @Test
    fun testJump() {
        writeVCD = true

        logicAnalyzer.module("jumpRegister") {
            reg(kcpu.jumpRegister.checkRegister, "checkRegister")
            wire(kcpu.jumpRegister.shouldCheckFlag.q, "SCF")
            wire(kcpu.jumpRegister.shouldJumpFlag.q, "SJF")
            wire(kcpu.programCounter.incDisableFlag, "IDF")
            wire(kcpu.jumpRegister.checkResult.output, "checkResult")
        }

        asm {
            label("start")

            mov(JUMP_A, 0)
            jmp("exit")

            mov(JUMP_A, 1)
            jmp("start")

            label("exit")
        }.writeInto(rom.data)


        for (i in 0..21 / 3) {
            for (j in 0..2)
                print(Integer.toHexString(rom.data[i * 3 + j].toInt()) + " ")
            println()
        }
        simulateCycles(10)
        assert(0xA > kcpu.programCounter.data.outBus.asBits)
    }

    @Test
    fun testALU() {
        asm {
            label("start")
            inalu(15, 10)
            nop()
            nop()
            nop()
            jmp("start")
        }.writeInto(rom.data)
        simulateCycles(5)
        assertEquals(25, kcpu.alu.sum.internalValueBus.asBits)
        assertEquals(5, kcpu.alu.sub.internalValueBus.asBits)

        assertEquals(0, kcpu.alu.eq.internalValueBus.asBits)
        assertEquals(1, kcpu.alu.gt.internalValueBus.asBits)
        assertEquals(0, kcpu.alu.lt.internalValueBus.asBits)

        assertEquals(1, kcpu.alu.gteq.internalValueBus.asBits)
        assertEquals(0, kcpu.alu.lteq.internalValueBus.asBits)
    }
}