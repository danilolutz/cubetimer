package com.danilolutz.cubetimer.feature.timer

import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.domain.timer.TimerSession
import com.danilolutz.cubetimer.domain.timer.TimerState
import com.danilolutz.cubetimer.model.Scramble
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TimerViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `generates a scramble and saves a completed solve`() {
        val repository = FakeSolveRepository()
        val viewModel = viewModel(repository)

        dispatcher.scheduler.runCurrent()
        assertEquals("R U R' U'", viewModel.state.value.scramble)
        assertFalse(viewModel.state.value.isGeneratingScramble)

        viewModel.toggleTimer(100)
        viewModel.toggleTimer(1_234)

        assertEquals(TimerState.Stopped(1_134), viewModel.state.value.timerState)
        assertEquals(listOf(Solve(1_134, "R U R' U'", 9_000)), repository.solves)
    }

    @Test
    fun `deletes and restores a solve`() {
        val solve = Solve(1_000, "U", 1)
        val repository = FakeSolveRepository(listOf(solve))
        val viewModel = viewModel(repository)

        viewModel.deleteSolve(solve)
        assertTrue(repository.solves.isEmpty())

        viewModel.restoreSolve(solve)
        assertEquals(listOf(solve), repository.solves)
    }

    @Test
    fun `reports a scramble generation error`() {
        val viewModel = TimerViewModel(
            repository = FakeSolveRepository(),
            scrambleSource = FailingScrambleSource,
            scrambleDispatcher = dispatcher,
        )

        dispatcher.scheduler.runCurrent()

        assertEquals("Could not generate a scramble. Try again.", viewModel.state.value.errorMessage)
        assertFalse(viewModel.state.value.isGeneratingScramble)
        assertNull(viewModel.state.value.scramble)
    }

    private fun viewModel(repository: FakeSolveRepository): TimerViewModel = TimerViewModel(
        repository = repository,
        scrambleSource = FixedScrambleSource,
        session = TimerSession(),
        clock = { 9_000 },
        scrambleDispatcher = dispatcher,
    )
}

private class FakeSolveRepository(initialSolves: List<Solve> = emptyList()) : SolveRepository {
    var solves: List<Solve> = initialSolves

    override fun load(): List<Solve> = solves

    override fun save(solves: List<Solve>) {
        this.solves = solves
    }
}

private object FixedScrambleSource : ScrambleSource {
    override fun generate(): Scramble = requireNotNull(Scramble.parse("R U R' U'"))
}

private object FailingScrambleSource : ScrambleSource {
    override fun generate(): Scramble = error("TNoodle unavailable")
}
