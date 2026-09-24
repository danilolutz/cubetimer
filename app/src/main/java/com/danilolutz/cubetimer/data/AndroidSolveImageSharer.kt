package com.danilolutz.cubetimer.data

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import com.danilolutz.cubetimer.feature.share.SolveImageSharer
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AndroidSolveImageSharer(
    private val context: Context,
    private val startActivity: (Intent) -> Unit = { intent -> context.startActivity(intent) },
) : SolveImageSharer {
    override suspend fun share(bitmap: Bitmap, completedAtMillis: Long) {
        val imageFile = withContext(Dispatchers.IO) {
            save(bitmap, completedAtMillis)
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile,
        )
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            clipData = ClipData.newRawUri("CubeTimer solve", uri)
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, null))
    }

    private fun save(bitmap: Bitmap, completedAtMillis: Long): File {
        val directory = File(context.cacheDir, SHARE_DIRECTORY).apply { mkdirs() }
        directory.listFiles()?.forEach(File::delete)
        val imageFile = File(directory, "cubetimer-solve-$completedAtMillis.png")
        FileOutputStream(imageFile).use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, output)) {
                "Could not encode solve image"
            }
        }
        return imageFile
    }

    private companion object {
        const val SHARE_DIRECTORY = "shared_solves"
        const val PNG_QUALITY = 100
    }
}
