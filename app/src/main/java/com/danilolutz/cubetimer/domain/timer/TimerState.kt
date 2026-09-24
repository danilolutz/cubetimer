package com.danilolutz.cubetimer.domain.timer

sealed interface TimerState {
    data object Ready : TimerState

    data class Running(val startedAtMillis: Long) : TimerState

    data class Stopped(val durationMillis: Long) : TimerState
}
