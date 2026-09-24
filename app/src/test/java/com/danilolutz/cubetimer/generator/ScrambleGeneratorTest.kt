package com.danilolutz.cubetimer.generator

import cs.min2phase.Tools
import com.danilolutz.cubetimer.model.Cube
import com.danilolutz.cubetimer.model.Scramble
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random

class ScrambleGeneratorTest {
    private val generator = TnoodleThreeByThreeScrambler()

    @Test
    fun `generates a parseable TNoodle 3x3 scramble`() {
        val scramble = generator.generate()

        assertNotNull(scramble)
        assertTrue(scramble.moves.isNotEmpty())
        assertTrue(scramble.moves.size <= 21)
    }

    @Test
    fun `generates only outer layer moves supported by the cube model`() {
        val scramble = generator.generate()

        assertTrue(scramble.notation.matches(Regex("[URFDLB][2']?(?: [URFDLB][2']?)*")))
    }

    @Test
    fun `generated notation can be parsed and applied to the cube map`() {
        val notation = generator.generate().notation

        val parsed = Scramble.parse(notation)

        assertNotNull(parsed)
        Cube().apply(requireNotNull(parsed))
    }

    @Test
    fun `generated scramble recreates each deterministic random state`() {
        val generatorRandom = Random(RANDOM_SEED)
        val expectedStateRandom = Random(RANDOM_SEED)
        val deterministicGenerator = TnoodleThreeByThreeScrambler(generatorRandom)

        repeat(RANDOM_STATE_SAMPLES) {
            val expectedState = Tools.randomCube(expectedStateRandom)
            val scramble = deterministicGenerator.generate()

            assertEquals(expectedState, Tools.fromScramble(scramble.notation))
        }
    }

    private companion object {
        const val RANDOM_SEED = 8_675_309L
        const val RANDOM_STATE_SAMPLES = 20
    }
}
