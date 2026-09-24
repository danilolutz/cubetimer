package com.danilolutz.cubetimer.ui.cube

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.danilolutz.cubetimer.model.Cube
import com.danilolutz.cubetimer.model.Face
import com.danilolutz.cubetimer.model.Scramble
import com.danilolutz.cubetimer.model.WcaDisplayProjection

@androidx.compose.runtime.Composable
fun CubeMap(
    colors: List<Face>,
    modifier: Modifier = Modifier,
    stickerSize: Dp = DEFAULT_STICKER_SIZE,
) {
    val net = WcaDisplayProjection.project(colors)
    val faceWidth = stickerSize * FACE_WIDTH + FACE_GAP
    val mapWidth = faceWidth * MAP_WIDTH_IN_FACES
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.width(mapWidth)) {
            Spacer(Modifier.weight(1f))
            FaceGrid(net.top, stickerSize = stickerSize)
            Spacer(Modifier.weight(2f))
        }
        Row {
            FaceGrid(net.left, stickerSize = stickerSize)
            FaceGrid(net.front, stickerSize = stickerSize)
            FaceGrid(net.right, stickerSize = stickerSize)
            FaceGrid(net.back, stickerSize = stickerSize)
        }
        Row(Modifier.width(mapWidth)) {
            Spacer(Modifier.weight(1f))
            FaceGrid(net.bottom, stickerSize = stickerSize)
            Spacer(Modifier.weight(2f))
        }
    }
}

@androidx.compose.runtime.Composable
private fun FaceGrid(colors: List<Face>, stickerSize: Dp) {
    Column(Modifier.padding(STICKER_GAP)) {
        colors.chunked(FACE_WIDTH).forEach { row ->
            Row {
                row.forEach { face ->
                    Spacer(
                        Modifier
                            .size(stickerSize)
                            .padding(STICKER_GAP)
                            .clip(RoundedCornerShape(STICKER_CORNER_RADIUS))
                            .background(face.color),
                    )
                }
            }
        }
    }
}

fun cubeColorsFor(notation: String): List<Face> {
    val cube = Cube()
    Scramble.parse(notation)?.let(cube::apply)
    return cube.colors
}

private val Face.color: Color
    get() = when (this) {
        Face.U -> Color.White
        Face.D -> Color(0xFFFFEB3B)
        Face.L -> Color(0xFFFF8F00)
        Face.F -> Color(0xFF43A047)
        Face.R -> Color(0xFFD32F2F)
        Face.B -> Color(0xFF1976D2)
    }

private const val FACE_WIDTH = 3
private const val MAP_WIDTH_IN_FACES = 4
private val FACE_GAP = 2.dp
private val STICKER_GAP = 1.dp
private val STICKER_CORNER_RADIUS = 4.dp
private val DEFAULT_STICKER_SIZE = 23.12.dp
