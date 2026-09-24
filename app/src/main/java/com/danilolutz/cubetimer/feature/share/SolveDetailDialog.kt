package com.danilolutz.cubetimer.feature.share

import android.graphics.Bitmap
import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.drawToBitmap
import com.composables.icons.lucide.R as LucideR
import com.danilolutz.cubetimer.domain.solve.Solve
import com.danilolutz.cubetimer.ui.cube.CubeMap
import com.danilolutz.cubetimer.ui.cube.cubeColorsFor
import com.danilolutz.cubetimer.ui.format.formatSolveDateTime
import com.danilolutz.cubetimer.ui.format.formatSolveDialogTime

@Composable
fun SolveDetailDialog(
    solve: Solve,
    sharer: SolveImageSharer,
    isShareRequested: Boolean,
    onShareRequested: () -> Unit,
    onShareFinished: (String?) -> Unit,
    onDismiss: () -> Unit,
) {
    var cardBounds by remember { mutableStateOf<Rect?>(null) }
    var rootView by remember { mutableStateOf<View?>(null) }
    val windowSize = LocalWindowInfo.current.containerSize
    val density = LocalDensity.current
    val dialogWidth = with(density) {
        dialogWidth(windowSize.width.toDp(), windowSize.height.toDp())
    }

    LaunchedEffect(isShareRequested) {
        if (!isShareRequested) {
            return@LaunchedEffect
        }
        val bitmap = rootView?.drawToBitmap()?.cropTo(cardBounds)
        if (bitmap == null) {
            onShareFinished("Could not prepare the image to share.")
            return@LaunchedEffect
        }
        val errorMessage = runCatching {
            sharer.share(bitmap, solve.completedAtMillis)
        }.exceptionOrNull()?.let { "Could not share the solve." }
        onShareFinished(errorMessage)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val currentView = LocalView.current
        SideEffect { rootView = currentView }
        Surface(
            modifier = Modifier.width(dialogWidth),
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
        ) {
            Column(Modifier.padding(24.dp)) {
                SolveShareCard(
                    solve = solve,
                    modifier = Modifier.onGloballyPositioned { cardBounds = it.boundsInRoot() },
                )
                DialogActions(isShareRequested, onShareRequested, onDismiss)
            }
        }
    }
}

@Composable
private fun DialogActions(
    isSharing: Boolean,
    onShareRequested: () -> Unit,
    onDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onShareRequested, enabled = !isSharing) {
            if (isSharing) {
                CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Icon(
                    painter = painterResource(LucideR.drawable.lucide_ic_share),
                    contentDescription = "Share solve",
                )
            }
        }
        TextButton(onClick = onDismiss) {
            Text("Close")
        }
    }
}

@Composable
private fun SolveShareCard(solve: Solve, modifier: Modifier) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = formatSolveDialogTime(solve.durationMillis),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = formatSolveDateTime(solve.completedAtMillis),
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        )
        Text(
            text = solve.scramble,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall,
        )
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
            contentAlignment = Alignment.Center,
        ) {
            val stickerSize = minOf(20.dp, (maxWidth - 8.dp) / 12f)
            CubeMap(cubeColorsFor(solve.scramble), stickerSize = stickerSize)
        }
    }
}

private fun dialogWidth(screenWidth: androidx.compose.ui.unit.Dp, screenHeight: androidx.compose.ui.unit.Dp) =
    (screenWidth * if (screenWidth <= screenHeight) 0.87f else 0.65f)
        .coerceIn(280.dp, 560.dp)
        .coerceAtMost((screenWidth - 32.dp).coerceAtLeast(0.dp))

private fun Bitmap.cropTo(bounds: Rect?): Bitmap? {
    bounds ?: return null
    val left = bounds.left.toInt().coerceIn(0, width - 1)
    val top = bounds.top.toInt().coerceIn(0, height - 1)
    val right = bounds.right.toInt().coerceIn(left + 1, width)
    val bottom = bounds.bottom.toInt().coerceIn(top + 1, height)
    return Bitmap.createBitmap(this, left, top, right - left, bottom - top)
}
