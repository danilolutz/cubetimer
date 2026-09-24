package com.danilolutz.cubetimer.feature.timer

import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.domain.timer.TimerState

data class TimerUiState(
    val scramble: String? = null,
    val isGeneratingScramble: Boolean = false,
    val timerState: TimerState = TimerState.Ready,
    val solves: List<Solve> = emptyList(),
    val selectedSolve: Solve? = null,
    val isShareRequested: Boolean = false,
    val errorMessage: String? = null,
)
