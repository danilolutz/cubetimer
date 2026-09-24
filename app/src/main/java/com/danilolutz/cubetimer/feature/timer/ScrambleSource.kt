package com.danilolutz.cubetimer.feature.timer

import com.danilolutz.cubetimer.model.Scramble

interface ScrambleSource {
    fun generate(): Scramble
}
