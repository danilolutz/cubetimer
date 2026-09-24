package com.danilolutz.cubetimer.model

data class Scramble(
    val moves: List<Move>
) {
    val notation: String
        get() = moves.joinToString(" ") { it.notation }

    companion object {
        fun parse(notation: String): Scramble? {
            val movesByNotation = Move.entries.associateBy(Move::notation)
            val tokens = notation.trim().split(Regex("\\s+")).filter(String::isNotBlank)
            return tokens.map { movesByNotation[it] ?: return null }.let(::Scramble)
        }
    }
}
