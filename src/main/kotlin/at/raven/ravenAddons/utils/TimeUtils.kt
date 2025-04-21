package at.raven.ravenAddons.utils

import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ServerTimeMark.Companion.inWholeTicks
import at.raven.ravenAddons.utils.ServerTimeMark.Companion.ticks
import kotlin.time.Duration

@LoadModule
object TimeUtils {
    fun Duration.format(): String {
        return buildString {
            if (isNegative()) append('-')
            absoluteValue.toComponents { days, hours, minutes, seconds, nanoseconds ->
                val ms = (nanoseconds / 1_000_000).toString().padStart(3, '0')
                val parts = mutableListOf<String>()

                if (days > 0) parts += "${days}d"
                if (hours > 0 || parts.isNotEmpty()) parts += "${hours}h"
                if (minutes > 0 || parts.isNotEmpty()) parts += "${minutes}m"
                if (seconds > 0 || parts.isNotEmpty()) parts += "§c${seconds}s"
                parts += "§6${ms}ms"

                append(parts.joinToString(" "))
            }
        }
    }

    fun Duration.clampTicks(): Duration = inWholeTicks.ticks
}