package com.danilolutz.cubetimer.generator

import cs.min2phase.SearchWCA
import cs.min2phase.Tools
import com.danilolutz.cubetimer.feature.timer.ScrambleSource
import com.danilolutz.cubetimer.model.Scramble
import java.util.Random

/**
 * Local port of TNoodle-lib 0.19.2's 3x3 random-state generator.
 *
 * The WCA implementation first samples a legal cube state and then asks
 * min2phase for its inverse solution, limited to 21 moves.
 */
class TnoodleThreeByThreeScrambler(
    private val random: Random = Random()
) : ScrambleSource {
    private val searcher = SearchWCA()

    override fun generate(): Scramble {
        val randomState = Tools.randomCube(random)
        val notation = searcher.solution(
            randomState,
            MAX_SCRAMBLE_LENGTH,
            TIMEOUT_MILLIS,
            MIN_SEARCH_MILLIS,
            SearchWCA.INVERSE_SOLUTION
        ).trim().also { result ->
            check(!result.startsWith("Error")) { "TNoodle min2phase failed: $result" }
        }
        return requireNotNull(Scramble.parse(notation)) {
            "TNoodle produced unsupported 3x3 notation: $notation"
        }
    }

    private companion object {
        const val MAX_SCRAMBLE_LENGTH = 21
        const val TIMEOUT_MILLIS = 60_000L
        const val MIN_SEARCH_MILLIS = 200L
    }
}
