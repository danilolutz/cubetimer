package com.danilolutz.cubetimer.ui

import android.os.SystemClock
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.R as LucideR
import com.danilolutz.cubetimer.domain.timer.TimerState
import com.danilolutz.cubetimer.feature.history.HistoryScreen
import com.danilolutz.cubetimer.feature.share.SolveDetailDialog
import com.danilolutz.cubetimer.feature.share.SolveImageSharer
import com.danilolutz.cubetimer.feature.timer.TimerScreen
import com.danilolutz.cubetimer.feature.timer.TimerViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    viewModel: TimerViewModel,
    solveImageSharer: SolveImageSharer,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val pendingUndoJobs = remember { mutableListOf<Job>() }
    var tab by remember { mutableIntStateOf(TIMER_TAB) }
    var nowMillis by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    var isClearHistoryConfirmationVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.timerState is TimerState.Running) {
        if (state.timerState !is TimerState.Running) {
            return@LaunchedEffect
        }
        while (true) {
            withFrameNanos {
                nowMillis = SystemClock.elapsedRealtime()
            }
        }
    }
    LaunchedEffect(state.errorMessage) {
        val errorMessage = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(errorMessage)
        viewModel.dismissError()
    }

    Scaffold(
        snackbarHost = { CubeTimerSnackbarHost(snackbarHostState) },
        bottomBar = { NavigationTabs(tab, onTabChanged = { tab = it }) },
        floatingActionButton = {
            if (tab == TIMER_TAB) {
                NextScrambleButton(state.isGeneratingScramble, viewModel::generateScramble)
            }
        },
    ) { padding ->
        when (tab) {
            TIMER_TAB -> TimerScreen(
                scramble = state.scramble.orEmpty(),
                elapsedMillis = viewModel.elapsedMillis(nowMillis),
                timerState = state.timerState,
                isReady = !state.isGeneratingScramble,
                onToggleTimer = { viewModel.toggleTimer(SystemClock.elapsedRealtime()) },
                modifier = Modifier.padding(padding),
            )
            HISTORY_TAB -> HistoryScreen(
                solves = state.solves,
                onOpenSolve = viewModel::openSolveDetails,
                onDeleteSolve = { solve ->
                    viewModel.deleteSolve(solve)
                    pendingUndoJobs.removeAll(Job::isCompleted)
                    val undoJob = coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Solve deleted",
                            actionLabel = "Undo",
                            withDismissAction = true,
                        )
                        if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                            viewModel.restoreSolve(solve)
                        }
                    }
                    pendingUndoJobs.add(undoJob)
                },
                onClearHistory = { isClearHistoryConfirmationVisible = true },
                modifier = Modifier.padding(padding),
            )
        }
    }
    state.selectedSolve?.let { solve ->
        SolveDetailDialog(
            solve = solve,
            sharer = solveImageSharer,
            isShareRequested = state.isShareRequested,
            onShareRequested = viewModel::requestShare,
            onShareFinished = viewModel::finishShare,
            onDismiss = viewModel::closeSolveDetails,
        )
    }
    if (isClearHistoryConfirmationVisible) {
        AlertDialog(
            onDismissRequest = { isClearHistoryConfirmationVisible = false },
            title = { Text("Clear history?") },
            text = { Text("All solves will be deleted. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    pendingUndoJobs.forEach(Job::cancel)
                    pendingUndoJobs.clear()
                    snackbarHostState.currentSnackbarData?.dismiss()
                    viewModel.clearHistory()
                    isClearHistoryConfirmationVisible = false
                }) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { isClearHistoryConfirmationVisible = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun CubeTimerSnackbarHost(hostState: SnackbarHostState) {
    SnackbarHost(hostState = hostState) { snackbarData ->
        Snackbar(
            snackbarData = snackbarData,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            actionColor = MaterialTheme.colorScheme.primary,
            dismissActionContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun NavigationTabs(selectedTab: Int, onTabChanged: (Int) -> Unit) {
    Surface(Modifier.fillMaxWidth(), color = NavigationBarDefaults.containerColor) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(56.dp)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            listOf("Timer", "Statistics").forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = isSelected,
                            onClick = { onTabChanged(index) },
                            role = Role.Tab,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
private fun NextScrambleButton(isLoading: Boolean, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.semantics {
            if (isLoading) {
                disabled()
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.semantics {
                contentDescription = "Generating official scramble"
            })
        } else {
            Icon(
                painter = painterResource(LucideR.drawable.lucide_ic_refresh_cw),
                contentDescription = "Next scramble",
            )
        }
    }
}

private const val TIMER_TAB = 0
private const val HISTORY_TAB = 1
