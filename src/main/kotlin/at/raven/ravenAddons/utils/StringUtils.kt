package at.raven.ravenAddons.utils

import at.raven.ravenAddons.utils.RegexUtils.findAll

object StringUtils {
    private val minecraftColorCodesPattern = "(?i)(§[0-9a-fklmnor])+".toPattern()

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

    fun String?.equalsIgnoreColor(string: String?) = this?.let { it.removeColors() == string?.removeColors() } == true

    fun String.cleanupColors(): String {
        var message = this
        while (message.startsWith("§r")) {
            message = message.substring(2)
        }
        while (message.endsWith("§r")) {
            message = message.substring(0, message.length - 2)
        }
        return message
    }

    fun <T : Enum<T>> T.toFormattedName() =
        name.split("_").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }

    fun String.lastColorCode(): String? = minecraftColorCodesPattern.findAll(this).lastOrNull()
}