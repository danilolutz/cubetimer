package com.danilolutz.cubetimer.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CubeScrambleTest {
    @Test fun `each move followed by its inverse resolves cube`() {
        mapOf(
            Move.U to Move.U_PRIME, Move.D to Move.D_PRIME, Move.L to Move.L_PRIME,
            Move.R to Move.R_PRIME, Move.F to Move.F_PRIME, Move.B to Move.B_PRIME
        ).forEach { (move, inverse) ->
            val cube = Cube().apply(move).apply(inverse)
            assertEquals(solvedColors(), cube.colors)
        }
    }

    @Test fun `four quarter turns resolve every face`() {
        listOf(Move.U, Move.D, Move.L, Move.R, Move.F, Move.B).forEach { move ->
            val cube = Cube()
            repeat(4) { cube.apply(move) }
            assertEquals(solvedColors(), cube.colors)
        }
    }

    @Test fun `double turns match two quarter turns`() {
        listOf(Move.U to Move.U2, Move.D to Move.D2, Move.L to Move.L2, Move.R to Move.R2, Move.F to Move.F2, Move.B to Move.B2).forEach { (quarter, double) ->
            val twice = Cube().apply(quarter).apply(quarter)
            assertEquals(twice.colors, Cube().apply(double).colors)
        }
    }

    @Test fun `parsing preserves notation and changes visual state`() {
        val scramble = Scramble.parse("R U R' U' F2")
        assertNotNull(scramble)
        assertEquals("R U R' U' F2", scramble?.notation)
        assertNotEquals(solvedColors(), Cube().apply(scramble!!).colors)
    }

    @Test fun `invalid notation is rejected`() = assertEquals(null, Scramble.parse("R X U"))

    private fun solvedColors() = Cube().colors
}
