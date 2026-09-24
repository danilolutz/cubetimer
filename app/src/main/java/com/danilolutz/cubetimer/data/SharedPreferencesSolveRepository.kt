package com.danilolutz.cubetimer.data

import android.content.SharedPreferences
import androidx.core.content.edit
import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.feature.timer.SolveRepository

class SharedPreferencesSolveRepository(
    private val preferences: SharedPreferences,
) : SolveRepository {
    init {
        migrateLegacyHistoryOnce()
    }

    override fun load(): List<Solve> = preferences.getStringSet(SOLVE_HISTORY_KEY, emptySet())
        .orEmpty()
        .mapNotNull(::decode)
        .sortedByDescending(Solve::completedAtMillis)

    override fun save(solves: List<Solve>) {
        preferences.edit {
            putStringSet(SOLVE_HISTORY_KEY, solves.take(MAX_SOLVES).map(::encode).toSet())
        }
    }

    private fun migrateLegacyHistoryOnce() {
        if (preferences.getBoolean(MIGRATION_COMPLETED_KEY, false)) {
            return
        }
        preferences.edit {
            remove(LEGACY_SOLVES_KEY)
            putBoolean(MIGRATION_COMPLETED_KEY, true)
        }
    }

    private fun encode(solve: Solve): String =
        "${solve.durationMillis}|${solve.completedAtMillis}|${solve.scramble}"

    private fun decode(value: String): Solve? {
        val parts = value.split('|', limit = SERIALIZED_PARTS)
        if (parts.size != SERIALIZED_PARTS) {
            return null
        }
        val durationMillis = parts[0].toLongOrNull() ?: return null
        val completedAtMillis = parts[1].toLongOrNull() ?: return null
        return Solve(durationMillis, parts[2], completedAtMillis)
    }

    private companion object {
        const val LEGACY_SOLVES_KEY = "solves"
        const val SOLVE_HISTORY_KEY = "solve_history_v2"
        const val MIGRATION_COMPLETED_KEY = "solve_history_v2_migration_completed"
        const val SERIALIZED_PARTS = 3
        const val MAX_SOLVES = 200
    }
}
