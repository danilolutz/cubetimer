package com.danilolutz.cubetimer.feature.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.danilolutz.cubetimer.domain.timer.TimerState
import com.danilolutz.cubetimer.ui.cube.CubeMap
import com.danilolutz.cubetimer.ui.cube.cubeColorsFor

@Composable
fun TimerScreen(
    scramble: String,
    elapsedMillis: Long,
    timerState: TimerState,
    isReady: Boolean,
    onToggleTimer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cubeColors = remember(scramble) { cubeColorsFor(scramble) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SCREEN_PADDING),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = scramble,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            minLines = SCRAMBLE_LINES,
        )
        CubeMap(cubeColors, Modifier.padding(vertical = CUBE_VERTICAL_PADDING))
        TimerReadout(
            elapsedMillis = elapsedMillis,
            isRunning = timerState is TimerState.Running,
            enabled = isReady,
            onToggleTimer = onToggleTimer,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = READOUT_BOTTOM_PADDING),
        )
    }
}

@Composable
private fun TimerReadout(
    elapsedMillis: Long,
    isRunning: Boolean,
    enabled: Boolean,
    onToggleTimer: () -> Unit,
    modifier: Modifier,
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val gradientColors = if (backgroundColor.luminance() < DARK_LUMINANCE_THRESHOLD) {
        listOf(Color(0xFF393448), Color(0xFF292632), backgroundColor)
    } else {
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.30f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
            backgroundColor,
        )
    }
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Brush.radialGradient(gradientColors))
            .clickable(enabled = enabled, onClick = onToggleTimer),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            TimerDigits(elapsedMillis)
            Text(
                text = if (isRunning) "Tap to stop" else "Tap to start",
                modifier = Modifier.padding(top = 14.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TimerDigits(elapsedMillis: Long) {
    Row(verticalAlignment = Alignment.Bottom) {
        Text(
            text = "%d:".format(elapsedMillis / MILLIS_PER_MINUTE),
            fontSize = 56.sp,
            fontWeight = FontWeight.Light,
        )
        Text(
            text = "%02d".format((elapsedMillis / MILLIS_PER_SECOND) % SECONDS_PER_MINUTE),
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = ".%02d".format((elapsedMillis % MILLIS_PER_SECOND) / MILLIS_PER_CENTISECOND),
            fontSize = 24.sp,
            modifier = Modifier.padding(bottom = 7.dp),
        )
    }
}

private const val SCRAMBLE_LINES = 2
private const val DARK_LUMINANCE_THRESHOLD = 0.5f
private const val MILLIS_PER_CENTISECOND = 10
private const val MILLIS_PER_SECOND = 1_000
private const val SECONDS_PER_MINUTE = 60
private const val MILLIS_PER_MINUTE = MILLIS_PER_SECOND * SECONDS_PER_MINUTE
private val SCREEN_PADDING = 20.dp
private val CUBE_VERTICAL_PADDING = 22.dp
private val READOUT_BOTTOM_PADDING = 56.dp
