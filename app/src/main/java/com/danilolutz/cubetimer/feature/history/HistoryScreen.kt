package com.danilolutz.cubetimer.feature.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.R as LucideR
import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.domain.statistics.SolveStatistics
import com.danilolutz.cubetimer.domain.statistics.calculateSolveStatistics
import com.danilolutz.cubetimer.ui.format.formatTime

@Composable
fun HistoryScreen(
    solves: List<Solve>,
    onOpenSolve: (Solve) -> Unit,
    onDeleteSolve: (Solve) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statistics = calculateSolveStatistics(solves)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(SCREEN_PADDING),
    ) {
        HistoryHeader(solves.isNotEmpty(), onClearHistory)
        StatisticsCard(statistics)
        if (solves.isEmpty()) {
            Text("Your solves will appear here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            return@Column
        }
        Spacer(Modifier.padding(top = HISTORY_SPACING))
        SolveList(solves, onOpenSolve, onDeleteSolve)
    }
}

@Composable
private fun HistoryHeader(hasSolves: Boolean, onClearHistory: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Statistics", style = MaterialTheme.typography.titleLarge)
        if (hasSolves) {
            TextButton(onClick = onClearHistory) {
                Text("Clear all")
            }
        }
    }
}

@Composable
private fun SolveList(
    solves: List<Solve>,
    onOpenSolve: (Solve) -> Unit,
    onDeleteSolve: (Solve) -> Unit,
) {
    LazyColumn {
        itemsIndexed(solves, key = { _, solve -> solve.completedAtMillis }) { index, solve ->
            SolveRow(solve, solves.size - index, onOpenSolve, onDeleteSolve)
        }
    }
}

@Composable
private fun SolveRow(
    solve: Solve,
    solveNumber: Int,
    onOpenSolve: (Solve) -> Unit,
    onDeleteSolve: (Solve) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onOpenSolve(solve) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 4.dp, top = 1.dp, bottom = 1.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = solveNumber.toString(),
                modifier = Modifier.width(SOLVE_NUMBER_WIDTH),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
            Text(
                text = formatTime(solve.durationMillis),
                modifier = Modifier.widthIn(min = 88.dp, max = 108.dp),
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = solve.scramble,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            IconButton(
                onClick = { onDeleteSolve(solve) },
                modifier = Modifier.semantics { contentDescription = "Delete solve" },
            ) {
                Icon(
                    painter = painterResource(LucideR.drawable.lucide_ic_trash_2),
                    contentDescription = null,
                    modifier = Modifier.size(DELETE_ICON_SIZE),
                )
            }
        }
    }
}

@Composable
private fun StatisticsCard(statistics: SolveStatistics) {
    Card(Modifier.fillMaxWidth().padding(top = 12.dp)) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            StatisticsGroup(
                listOf("Best", "Worst", "Mean"),
                listOf(
                    statistics.bestMillis?.let(::formatTime) ?: "—",
                    statistics.worstMillis?.let(::formatTime) ?: "—",
                    statistics.averageMillis?.let(::formatTime) ?: "—",
                ),
            )
            StatisticsGroup(
                listOf("ao5", "ao12", "Solves"),
                listOf(
                    statistics.ao5Millis?.let(::formatTime) ?: "—",
                    statistics.ao12Millis?.let(::formatTime) ?: "—",
                    statistics.count.toString(),
                ),
            )
            StatisticsGroup(
                listOf("Best ao5", "Best ao12"),
                listOf(
                    statistics.bestAo5Millis?.let(::formatTime) ?: "—",
                    statistics.bestAo12Millis?.let(::formatTime) ?: "—",
                ),
            )
        }
    }
}

@Composable
private fun StatisticsGroup(labels: List<String>, values: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        StatisticsLine(labels, MaterialTheme.typography.bodySmall, FontWeight.Normal)
        StatisticsLine(values, MaterialTheme.typography.titleSmall, FontWeight.SemiBold)
    }
}

@Composable
private fun StatisticsLine(
    values: List<String>,
    textStyle: androidx.compose.ui.text.TextStyle,
    fontWeight: FontWeight,
) {
    Row(Modifier.fillMaxWidth()) {
        repeat(STATISTICS_COLUMNS) { index ->
            Text(
                text = values.getOrNull(index).orEmpty(),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = textStyle,
                fontWeight = fontWeight,
            )
        }
    }
}

private const val STATISTICS_COLUMNS = 3
private val SCREEN_PADDING = 20.dp
private val HISTORY_SPACING = 12.dp
private val SOLVE_NUMBER_WIDTH = 36.dp
private val DELETE_ICON_SIZE = 16.dp
