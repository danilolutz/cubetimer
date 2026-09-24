package com.danilolutz.cubetimer.domain.solve

data class Solve(
    val durationMillis: Long,
    val scramble: String,
    val completedAtMillis: Long,
)
