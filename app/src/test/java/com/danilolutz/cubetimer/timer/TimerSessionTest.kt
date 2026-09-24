package com.danilolutz.cubetimer.timer

import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.domain.statistics.calculateSolveStatistics
import com.danilolutz.cubetimer.domain.timer.TimerSession
import com.danilolutz.cubetimer.domain.timer.TimerState
import com.danilolutz.cubetimer.ui.format.formatTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
class TimerSessionTest {
 @Test fun running_state_keeps_elapsed_time_live_and_stopping_freezes_it() {
  val timer = TimerSession()
  timer.toggle(100)
  assertTrue(timer.state is TimerState.Running)
  assertEquals(75L, timer.elapsedMillis(175))
  assertEquals(250L, timer.toggle(350))
  assertTrue(timer.state is TimerState.Stopped)
  assertEquals(250L, timer.elapsedMillis(500))
 }
 @Test fun reset_clears_running_or_stopped_time() {
  val timer = TimerSession()
  timer.toggle(100)
  assertEquals(75L, timer.elapsedMillis(175))
  timer.reset()
  assertTrue(timer.state is TimerState.Ready)
  assertEquals(0L, timer.elapsedMillis(1_000))

  timer.toggle(2_000)
  assertEquals(500L, timer.toggle(2_500))
  timer.reset()
  assertTrue(timer.state is TimerState.Ready)
  assertEquals(0L, timer.elapsedMillis(3_000))
 }
 @Test fun statistics_expose_best_and_average() { val stats = calculateSolveStatistics(listOf(Solve(1000, "U", 1), Solve(2000, "R", 2))); assertEquals(1000L, stats.bestMillis); assertEquals(1500L, stats.averageMillis); assertEquals(2, stats.count) }
 @Test fun time_formatting_is_readable() { assertTrue(formatTime(62_340).startsWith("1:02.34")) }
 @Test fun timer_formatting_uses_two_decimal_places() {
  assertEquals("0:00.00", formatTime(0))
  assertEquals("0:01.23", formatTime(1_230))
  assertEquals("1:02.34", formatTime(62_340))
 }
}
