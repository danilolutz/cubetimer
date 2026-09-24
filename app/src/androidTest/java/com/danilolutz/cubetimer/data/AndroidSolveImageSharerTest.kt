package com.danilolutz.cubetimer.data

import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.IntentCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidSolveImageSharerTest {
    @Test
    fun createsAnImagePngShareIntent() = runBlocking {
        var chooserIntent: Intent? = null
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharer = AndroidSolveImageSharer(context) { chooserIntent = it }
        val bitmap = Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)

        sharer.share(bitmap, 123)

        val chooser = requireNotNull(chooserIntent)
        val sendIntent = IntentCompat.getParcelableExtra(chooser, Intent.EXTRA_INTENT, Intent::class.java)
        assertNotNull(sendIntent)
        assertEquals("image/png", requireNotNull(sendIntent).type)
        assertNotNull(
            IntentCompat.getParcelableExtra(
                requireNotNull(sendIntent),
                Intent.EXTRA_STREAM,
                android.net.Uri::class.java,
            ),
        )
    }
}
