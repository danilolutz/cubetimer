package com.danilolutz.cubetimer.domain.statistics

import com.danilolutz.cubetimer.domain.solve.Solve

data class SolveStatistics(
    val count: Int,
    val bestMillis: Long?,
    val averageMillis: Long?,
    val worstMillis: Long?,
    val ao5Millis: Long?,
    val ao12Millis: Long?,
    val bestAo5Millis: Long?,
    val bestAo12Millis: Long?,
)

fun calculateSolveStatistics(solves: List<Solve>): SolveStatistics = SolveStatistics(
    count = solves.size,
    bestMillis = solves.minOfOrNull(Solve::durationMillis),
    averageMillis = solves.map(Solve::durationMillis).meanMillis(),
    worstMillis = solves.maxOfOrNull(Solve::durationMillis),
    ao5Millis = latestAverage(solves, AO5_SIZE, AO5_TRIM_COUNT),
    ao12Millis = latestAverage(solves, AO12_SIZE, AO12_TRIM_COUNT),
    bestAo5Millis = bestAverage(solves, AO5_SIZE, AO5_TRIM_COUNT),
    bestAo12Millis = bestAverage(solves, AO12_SIZE, AO12_TRIM_COUNT),
)

private fun latestAverage(solves: List<Solve>, windowSize: Int, trimCount: Int): Long? =
    solves.takeIf { it.size >= windowSize }
        ?.take(windowSize)
        ?.let { trimmedAverage(it, trimCount) }

private fun bestAverage(solves: List<Solve>, windowSize: Int, trimCount: Int): Long? =
    solves.windowed(windowSize)
        .mapNotNull { trimmedAverage(it, trimCount) }
        .minOrNull()

private fun trimmedAverage(solves: List<Solve>, trimCount: Int): Long? =
    solves.map(Solve::durationMillis)
        .sorted()
        .drop(trimCount)
        .dropLast(trimCount)
        .meanMillis()

private fun List<Long>.meanMillis(): Long? =
    takeIf(List<Long>::isNotEmpty)?.let { sum() / size }

private const val AO5_SIZE = 5
private const val AO5_TRIM_COUNT = 1
private const val AO12_SIZE = 12
private const val AO12_TRIM_COUNT = 2
