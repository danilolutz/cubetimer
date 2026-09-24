package com.danilolutz.cubetimer.model

/** Mutable 3×3 sticker model. Sticker order is U, R, F, D, L, B. */
class Cube {
    val stickers: MutableList<Sticker> = MutableList(54) { index ->
        Sticker(face = stickerFaces[index / 9], position = index % 9)
    }

    fun apply(move: Move): Cube {
        repeat(move.quarterTurns) { rotate(move.face) }
        return this
    }

    fun apply(scramble: Scramble): Cube {
        scramble.moves.forEach(::apply)
        return this
    }

    val colors: List<Face> get() = stickers.map(Sticker::face)

    private fun rotate(face: Face) {
        val original = stickers.toList()
        val normal = normalOf(face)
        val clockwiseAxis = normal.negated()
        geometry.forEachIndexed { sourceIndex, source ->
            if (source.center.dot(normal) == 1) {
                val destination = Geometry(
                    source.center.rotateAround(clockwiseAxis),
                    source.normal.rotateAround(clockwiseAxis)
                )
                stickers[geometry.indexOf(destination)] = original[sourceIndex]
            }
        }
    }

    private data class Vector(val x: Int, val y: Int, val z: Int) {
        fun dot(other: Vector) = x * other.x + y * other.y + z * other.z
        fun negated() = Vector(-x, -y, -z)
        fun rotateAround(axis: Vector): Vector {
            val axisComponent = axis.scale(dot(axis))
            val rotatedPerpendicular = Vector(
                axis.y * z - axis.z * y,
                axis.z * x - axis.x * z,
                axis.x * y - axis.y * x
            )
            return axisComponent + rotatedPerpendicular
        }
        private fun scale(factor: Int) = Vector(x * factor, y * factor, z * factor)
        private operator fun plus(other: Vector) = Vector(x + other.x, y + other.y, z + other.z)
    }

    private data class Geometry(val center: Vector, val normal: Vector)

    private companion object {
        val stickerFaces = listOf(Face.U, Face.R, Face.F, Face.D, Face.L, Face.B)
        val geometry: List<Geometry> = stickerFaces.flatMap(::faceGeometry)
        fun faceGeometry(face: Face): List<Geometry> = (0..8).map { position ->
            val row = position / 3 - 1; val column = position % 3 - 1
            when (face) {
                Face.U -> Geometry(Vector(column, 1, row), Vector(0, 1, 0))
                Face.D -> Geometry(Vector(column, -1, -row), Vector(0, -1, 0))
                Face.F -> Geometry(Vector(column, -row, 1), Vector(0, 0, 1))
                Face.B -> Geometry(Vector(-column, -row, -1), Vector(0, 0, -1))
                Face.R -> Geometry(Vector(1, -row, -column), Vector(1, 0, 0))
                Face.L -> Geometry(Vector(-1, -row, column), Vector(-1, 0, 0))
            }
        }
        fun normalOf(face: Face) = when (face) {
            Face.U -> Vector(0, 1, 0); Face.D -> Vector(0, -1, 0)
            Face.L -> Vector(-1, 0, 0); Face.R -> Vector(1, 0, 0)
            Face.F -> Vector(0, 0, 1); Face.B -> Vector(0, 0, -1)
        }
    }
}

private val Move.quarterTurns: Int get() = when (this) {
    Move.U_PRIME, Move.D_PRIME, Move.L_PRIME, Move.R_PRIME, Move.F_PRIME, Move.B_PRIME -> 3
    Move.U2, Move.D2, Move.L2, Move.R2, Move.F2, Move.B2 -> 2
    else -> 1
}
