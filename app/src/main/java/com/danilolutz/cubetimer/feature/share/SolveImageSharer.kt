package com.danilolutz.cubetimer.feature.share

import android.graphics.Bitmap

interface SolveImageSharer {
    suspend fun share(bitmap: Bitmap, completedAtMillis: Long)
}
