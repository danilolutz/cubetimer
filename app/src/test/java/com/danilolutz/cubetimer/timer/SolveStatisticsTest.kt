package com.danilolutz.cubetimer.timer

import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.domain.statistics.calculateSolveStatistics
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SolveStatisticsTest {
    @Test fun `empty history has no time statistics`() {
        val stats = calculateSolveStatistics(emptyList())

        assertEquals(0, stats.count)
        assertNull(stats.bestMillis)
        assertNull(stats.worstMillis)
        assertNull(stats.averageMillis)
        assertNull(stats.ao5Millis)
        assertNull(stats.ao12Millis)
        assertNull(stats.bestAo5Millis)
        assertNull(stats.bestAo12Millis)
    }

    @Test fun `single solve statistics cover the complete history`() {
        val stats = calculateSolveStatistics(
            listOf(
                solve(3_000, 3),
                solve(1_000, 1),
                solve(2_000, 2)
            )
        )

        assertEquals(3, stats.count)
        assertEquals(1_000L, stats.bestMillis)
        assertEquals(3_000L, stats.worstMillis)
        assertEquals(2_000L, stats.averageMillis)
        assertNull(stats.ao5Millis)
        assertNull(stats.ao12Millis)
    }

    @Test fun `ao5 trims extremes and reports latest and best windows`() {
        val stats = calculateSolveStatistics(solves(60, 50, 40, 30, 20, 10))

        assertEquals(40_000L, stats.ao5Millis)
        assertEquals(30_000L, stats.bestAo5Millis)
        assertNull(stats.ao12Millis)
        assertNull(stats.bestAo12Millis)
    }

    @Test fun `ao12 trims two extremes and reports latest and best windows`() {
        val stats = calculateSolveStatistics(solves(*(13 downTo 1).map { it.toLong() }.toLongArray()))

        assertEquals(7_500L, stats.ao12Millis)
        assertEquals(6_500L, stats.bestAo12Millis)
        assertEquals(11_000L, stats.ao5Millis)
        assertEquals(3_000L, stats.bestAo5Millis)
    }

    private fun solves(vararg seconds: Long) =
        seconds.mapIndexed { index, time -> solve(time * 1_000L, index.toLong()) }

    private fun solve(durationMillis: Long, completedAtMillis: Long) =
        Solve(durationMillis, "U", completedAtMillis)
}
