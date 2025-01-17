package at.raven.ravenAddons.utils

object StringUtils {
    fun String.removeColors(): String {
        val result = StringBuilder()

        var i = 0
        while (i < this.length) {
            if (this[i] == '§') {
                i += 2
            } else {
                result.append(this[i])
                i++
            }
        }

        return result.toString()
    }
}