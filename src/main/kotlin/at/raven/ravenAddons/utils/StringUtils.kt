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
}