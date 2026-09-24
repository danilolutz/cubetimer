package com.danilolutz.cubetimer.feature.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TimerViewModelFactory(
    private val repository: SolveRepository,
    private val scrambleSource: ScrambleSource,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            "Unsupported ViewModel: ${modelClass.name}"
        }
        return TimerViewModel(repository, scrambleSource) as T
    }
}
