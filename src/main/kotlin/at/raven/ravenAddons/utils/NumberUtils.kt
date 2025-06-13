package at.raven.ravenAddons.utils

import kotlin.math.pow

object NumberUtils {
    fun Double.roundTo(precision: Int): Double {
        val scale = 10.0.pow(precision)
        return kotlin.math.round(this * scale) / scale
    }
}
