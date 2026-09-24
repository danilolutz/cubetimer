package com.danilolutz.cubetimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.danilolutz.cubetimer.data.AndroidSolveImageSharer
import com.danilolutz.cubetimer.data.SharedPreferencesSolveRepository
import com.danilolutz.cubetimer.feature.timer.TimerViewModel
import com.danilolutz.cubetimer.feature.timer.TimerViewModelFactory
import com.danilolutz.cubetimer.feature.timer.SolveRepository
import com.danilolutz.cubetimer.generator.TnoodleThreeByThreeScrambler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.danilolutz.cubetimer.ui.MainScreen
import com.danilolutz.cubetimer.ui.theme.CubeTimerTheme

class MainActivity : ComponentActivity() {
    private val solveRepository: SolveRepository by lazy {
        SharedPreferencesSolveRepository(
            getSharedPreferences("cube_timer", MODE_PRIVATE)
        )
    }

    private val timerViewModel: TimerViewModel by lazy {
        ViewModelProvider(
            this,
            TimerViewModelFactory(solveRepository, TnoodleThreeByThreeScrambler())
        )[TimerViewModel::class.java]
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CubeTimerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(timerViewModel, AndroidSolveImageSharer(this@MainActivity))
                }
            }
        }
    }
}
