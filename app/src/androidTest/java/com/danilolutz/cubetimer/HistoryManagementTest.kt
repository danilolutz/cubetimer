package com.danilolutz.cubetimer

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import android.graphics.Bitmap
import com.danilolutz.cubetimer.data.SharedPreferencesSolveRepository
import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.feature.share.SolveImageSharer
import com.danilolutz.cubetimer.feature.timer.ScrambleSource
import com.danilolutz.cubetimer.feature.timer.TimerViewModel
import com.danilolutz.cubetimer.model.Scramble
import com.danilolutz.cubetimer.ui.MainScreen
import com.danilolutz.cubetimer.ui.theme.CubeTimerTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class HistoryManagementTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val preferenceName = "history-test-${UUID.randomUUID()}"
    private val preferences by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences(preferenceName, android.content.Context.MODE_PRIVATE)
    }
    private val solve = Solve(12_340, "R U R' U'", 1_700_000_000_000)

    @Before
    fun clearPreferences() {
        preferences.edit().clear().commit()
    }

    @After
    fun removePreferences() {
        preferences.edit().clear().commit()
    }

    @Test
    fun deletingSolveUpdatesHistoryAndUndoRestoresIt() {
        openHistoryWith(solve)

        composeRule.onNodeWithContentDescription("Delete solve").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Close").assertDoesNotExist()
        composeRule.onNodeWithText(solve.scramble).assertDoesNotExist()
        assertEquals(emptyList<Solve>(), repository().load())
        composeRule.onNodeWithText("0").assertExists()
        composeRule.onNodeWithText("Undo").performClick()
        composeRule.waitForIdle()

        assertEquals(listOf(solve), repository().load())
        composeRule.onNodeWithText(solve.scramble).assertExists()
    }

    @Test
    fun tappingSolveOpensDetailsAndInfoActionIsAbsent() {
        openHistoryWith(solve)

        composeRule.onNodeWithContentDescription("Solve details").assertDoesNotExist()
        composeRule.onNodeWithText(solve.scramble).performClick()

        composeRule.onNodeWithText("Close").assertExists()
        composeRule.onNodeWithText("12.34").assertExists()
        composeRule.onNodeWithContentDescription("Share solve").assertExists()
    }

    @Test
    fun cancelingClearKeepsHistoryAndConfirmingItEmptiesHistory() {
        openHistoryWith(solve)

        composeRule.onNodeWithText("Clear all").performClick()
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText(solve.scramble).assertExists()
        assertEquals(listOf(solve), repository().load())

        composeRule.onNodeWithText("Clear all").performClick()
        composeRule.onNodeWithText("Clear").performClick()
        composeRule.waitForIdle()

        assertEquals(emptyList<Solve>(), repository().load())
        composeRule.onNodeWithText("Your solves will appear here.").assertExists()
        composeRule.onNodeWithText("Clear all").assertDoesNotExist()
        composeRule.onNodeWithText("0").assertExists()
    }

    @Test
    fun clearingHistoryDismissesPendingUndo() {
        val remainingSolve = solve.copy(completedAtMillis = solve.completedAtMillis + 1)
        openHistoryWith(solve, remainingSolve)

        composeRule.onAllNodesWithContentDescription("Delete solve")[0].performClick()
        composeRule.onNodeWithText("Clear all").performClick()
        composeRule.onNodeWithText("Clear").performClick()
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Undo").assertDoesNotExist()
        assertEquals(emptyList<Solve>(), repository().load())
    }

    private fun openHistoryWith(vararg solves: Solve) {
        val repository = repository()
        repository.save(solves.toList())
        val viewModel = TimerViewModel(repository, TestScrambleSource)
        composeRule.setContent { CubeTimerTheme { MainScreen(viewModel, TestSolveImageSharer) } }
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Statistics").performClick()
        assertEquals(
            solves.size,
            composeRule.onAllNodesWithText(solve.scramble).fetchSemanticsNodes().size,
        )
    }

    private fun repository() = SharedPreferencesSolveRepository(preferences)
}

private object TestScrambleSource : ScrambleSource {
    override fun generate(): Scramble = requireNotNull(Scramble.parse("R U R' U'"))
}

private object TestSolveImageSharer : SolveImageSharer {
    override suspend fun share(bitmap: Bitmap, completedAtMillis: Long) = Unit
}
