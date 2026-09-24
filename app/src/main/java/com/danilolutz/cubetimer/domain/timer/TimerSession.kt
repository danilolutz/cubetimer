package com.danilolutz.cubetimer.domain.timer

class TimerSession {
    var state: TimerState = TimerState.Ready
        private set

    fun toggle(nowMillis: Long): Long? = when (val currentState = state) {
        TimerState.Ready,
        is TimerState.Stopped -> start(nowMillis)
        is TimerState.Running -> stop(currentState, nowMillis)
    }

    fun reset() {
        state = TimerState.Ready
    }

    fun elapsedMillis(nowMillis: Long): Long = when (val currentState = state) {
        TimerState.Ready -> 0
        is TimerState.Running -> (nowMillis - currentState.startedAtMillis).coerceAtLeast(0)
        is TimerState.Stopped -> currentState.durationMillis
    }

    private fun start(nowMillis: Long): Long? {
        state = TimerState.Running(nowMillis)
        return null
    }

    private fun stop(runningState: TimerState.Running, nowMillis: Long): Long {
        val durationMillis = (nowMillis - runningState.startedAtMillis).coerceAtLeast(0)
        state = TimerState.Stopped(durationMillis)
        return durationMillis
    }
}
