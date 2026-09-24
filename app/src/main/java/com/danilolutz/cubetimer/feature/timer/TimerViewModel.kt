package com.danilolutz.cubetimer.feature.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.domain.timer.TimerSession
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TimerViewModel(
    private val repository: SolveRepository,
    private val scrambleSource: ScrambleSource,
    private val session: TimerSession = TimerSession(),
    private val clock: () -> Long = System::currentTimeMillis,
    private val scrambleDispatcher: CoroutineDispatcher = Dispatchers.Default,
) : ViewModel() {
    private val mutableState = MutableStateFlow(TimerUiState(solves = repository.load()))
    val state: StateFlow<TimerUiState> = mutableState.asStateFlow()

    init {
        generateScramble()
    }

    fun generateScramble() {
        if (state.value.isGeneratingScramble) {
            return
        }
        session.reset()
        mutableState.value = state.value.copy(
            isGeneratingScramble = true,
            timerState = session.state,
            errorMessage = null,
        )
        viewModelScope.launch {
            runCatching {
                withContext(scrambleDispatcher) {
                    scrambleSource.generate().notation
                }
            }.onSuccess { notation ->
                mutableState.value = state.value.copy(
                    scramble = notation,
                    isGeneratingScramble = false,
                )
            }.onFailure {
                mutableState.value = state.value.copy(
                    isGeneratingScramble = false,
                    errorMessage = SCRAMBLE_ERROR_MESSAGE,
                )
            }
        }
    }

    fun toggleTimer(nowMillis: Long) {
        session.toggle(nowMillis)?.let(::saveCompletedSolve)
        mutableState.value = state.value.copy(timerState = session.state)
    }

    fun elapsedMillis(nowMillis: Long): Long = session.elapsedMillis(nowMillis)

    fun deleteSolve(solve: Solve) {
        updateSolves(state.value.solves.filterNot { it.completedAtMillis == solve.completedAtMillis })
    }

    fun restoreSolve(solve: Solve) {
        updateSolves((state.value.solves + solve).sortedByDescending(Solve::completedAtMillis))
    }

    fun clearHistory() {
        updateSolves(emptyList())
    }

    fun openSolveDetails(solve: Solve) {
        mutableState.value = state.value.copy(selectedSolve = solve)
    }

    fun closeSolveDetails() {
        mutableState.value = state.value.copy(selectedSolve = null, isShareRequested = false)
    }

    fun requestShare() {
        mutableState.value = state.value.copy(isShareRequested = true)
    }

    fun finishShare(errorMessage: String? = null) {
        mutableState.value = state.value.copy(
            isShareRequested = false,
            errorMessage = errorMessage,
        )
    }

    fun dismissError() {
        mutableState.value = state.value.copy(errorMessage = null)
    }

    private fun saveCompletedSolve(durationMillis: Long) {
        val scramble = state.value.scramble ?: return
        updateSolves(listOf(Solve(durationMillis, scramble, clock())) + state.value.solves)
    }

    private fun updateSolves(solves: List<Solve>) {
        repository.save(solves)
        mutableState.value = state.value.copy(solves = solves.take(MAX_SOLVES))
    }

    private companion object {
        const val MAX_SOLVES = 200
        const val SCRAMBLE_ERROR_MESSAGE = "Could not generate a scramble. Try again."
    }
}
