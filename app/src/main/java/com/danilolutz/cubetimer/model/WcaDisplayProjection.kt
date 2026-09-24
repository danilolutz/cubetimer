package com.danilolutz.cubetimer.model

/** Presents the WCA scramble orientation with white up and green in front. */
data class CubeNet(
    val top: List<Face>,
    val left: List<Face>,
    val front: List<Face>,
    val right: List<Face>,
    val back: List<Face>,
    val bottom: List<Face>
)

object WcaDisplayProjection {
    fun project(colors: List<Face>): CubeNet = CubeNet(
        top = colors.face(Face.U),
        left = colors.face(Face.L),
        front = colors.face(Face.F),
        right = colors.face(Face.R),
        back = colors.face(Face.B),
        bottom = colors.face(Face.D)
    )

    private fun List<Face>.face(face: Face): List<Face> = drop(face.index * FACE_SIZE).take(FACE_SIZE)

    private const val FACE_SIZE = 9
    private val Face.index: Int get() = when (this) {
        Face.U -> 0; Face.R -> 1; Face.F -> 2; Face.D -> 3; Face.L -> 4; Face.B -> 5
    }
}
