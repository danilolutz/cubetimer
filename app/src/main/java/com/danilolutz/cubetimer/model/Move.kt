package com.danilolutz.cubetimer.model

enum class Move (val face: Face, val notation: String) {
    U(Face.U, "U"),
    U_PRIME(Face.U,"U'"),
    U2(Face.U,"U2"),

    D(Face.D,"D"),
    D_PRIME(Face.D,"D'"),
    D2(Face.D,"D2"),

    L(Face.L,"L"),
    L_PRIME(Face.L,"L'"),
    L2(Face.L,"L2"),

    R(Face.R,"R"),
    R_PRIME(Face.R,"R'"),
    R2(Face.R,"R2"),

    F(Face.F,"F"),
    F_PRIME(Face.F,"F'"),
    F2(Face.F,"F2"),

    B(Face.B,"B"),
    B_PRIME(Face.B,"B'"),
    B2(Face.B,"B2")
}
