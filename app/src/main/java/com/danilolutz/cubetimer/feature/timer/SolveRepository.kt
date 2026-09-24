package com.danilolutz.cubetimer.feature.timer

import com.danilolutz.cubetimer.domain.solve.Solve

interface SolveRepository {
    fun load(): List<Solve>

    fun save(solves: List<Solve>)
}
