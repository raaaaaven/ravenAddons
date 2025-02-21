package at.raven.ravenAddons.utils

import java.awt.Color

class ColorUtils {
    fun Color.withAlpha(alpha: Int) = Color(this.red, this.green, this.blue, alpha)
}