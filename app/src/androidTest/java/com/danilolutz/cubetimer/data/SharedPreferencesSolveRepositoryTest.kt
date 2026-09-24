package com.danilolutz.cubetimer.data

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.danilolutz.cubetimer.domain.solve.Solve
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class SharedPreferencesSolveRepositoryTest {
    private val preferences by lazy {
        InstrumentationRegistry.getInstrumentation().targetContext.getSharedPreferences(
            "repository-test-${UUID.randomUUID()}",
            Context.MODE_PRIVATE,
        )
    }

    @Before
    fun setUp() {
        preferences.edit().clear().commit()
    }

    @After
    fun tearDown() {
        preferences.edit().clear().commit()
    }

    @Test
    fun firstUseDiscardsLegacyHistoryAndStartsEmpty() {
        preferences.edit().putStringSet("solves", setOf("1234|1|R U R' U'")).commit()

        val repository = SharedPreferencesSolveRepository(preferences)

        assertEquals(emptyList<Solve>(), repository.load())
        assertFalse(preferences.contains("solves"))
    }

    @Test
    fun keepsOnlyTheMostRecentTwoHundredSolves() {
        val repository = SharedPreferencesSolveRepository(preferences)
        val solves = (1L..201L).map { Solve(it, "U", it) }

        repository.save(solves)

        assertEquals(200, repository.load().size)
        assertEquals(200L, repository.load().first().completedAtMillis)
    }
}
