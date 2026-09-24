package com.danilolutz.cubetimer.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WcaDisplayProjectionTest {
    @Test fun `solved cube matches WCA color arrangement`() {
        val net = WcaDisplayProjection.project(Cube().colors)

        assertEquals(List(9) { Face.U }, net.top)
        assertEquals(List(9) { Face.L }, net.left)
        assertEquals(List(9) { Face.F }, net.front)
        assertEquals(List(9) { Face.R }, net.right)
        assertEquals(List(9) { Face.B }, net.back)
        assertEquals(List(9) { Face.D }, net.bottom)
    }

    @Test fun `scrambled map preserves every sticker on its WCA face`() {
        val cube = Cube().apply(requireNotNull(Scramble.parse("R U F' D2 L B")))
        val faces = cube.colors.chunked(9) // Cube sticker order: U, R, F, D, L, B.
        val net = WcaDisplayProjection.project(cube.colors)

        assertEquals(faces[0], net.top)
        assertEquals(faces[4], net.left)
        assertEquals(faces[2], net.front)
        assertEquals(faces[1], net.right)
        assertEquals(faces[5], net.back)
        assertEquals(faces[3], net.bottom)
    }
}
