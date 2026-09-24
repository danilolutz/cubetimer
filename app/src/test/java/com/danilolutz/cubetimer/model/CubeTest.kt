package com.danilolutz.cubetimer.model

import org.junit.Assert.assertEquals
import org.junit.Test

class CubeTest {
    @Test
    fun `U rotates the upper face`() {
        val cube = Cube()

        cube.apply(Move.U)

        assertEquals(Face.U, cube.stickers[0].face)
        assertEquals(Face.U, cube.stickers[2].face)
        assertEquals(Face.U, cube.stickers[6].face)
        assertEquals(Face.U, cube.stickers[8].face)
    }

    @Test
    fun `U rotates the upper face clockwise`() {
        val cube = Cube()

        val original = cube.stickers.subList(0, 9).toList()

        cube.apply(Move.U)

        assertEquals(original[6], cube.stickers[0])
        assertEquals(original[3], cube.stickers[1])
        assertEquals(original[0], cube.stickers[2])

        assertEquals(original[7], cube.stickers[3])
        assertEquals(original[4], cube.stickers[4])
        assertEquals(original[1], cube.stickers[5])

        assertEquals(original[8], cube.stickers[6])
        assertEquals(original[5], cube.stickers[7])
        assertEquals(original[2], cube.stickers[8])
    }
}