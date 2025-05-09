package at.raven.ravenAddons.utils

import at.raven.ravenAddons.event.managers.ServerManager
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@JvmInline
value class ServerTimeMark private constructor(val ticks: Long) : Comparable<ServerTimeMark> {

    operator fun minus(other: ServerTimeMark): Duration =
        (ticks - other.ticks).ticks

    operator fun plus(other: Duration) =
        ServerTimeMark(ticks + other.inWholeTicks)

    operator fun minus(other: Duration): ServerTimeMark = plus(-other)

    fun passedSince(): Duration = now() - this

    fun timeUntil(): Duration = -passedSince()

    fun isInPast(): Boolean = timeUntil().isNegative()

    fun isInFuture(): Boolean = timeUntil().isPositive()

    fun isFarPast(): Boolean = this == FAR_PAST

    fun isFarFuture(): Boolean = ticks == FAR_FUTURE_TICKS

    val formattedTime: String
        get() = TimeUtils.formatTicks(ticks.toInt())

    override fun compareTo(other: ServerTimeMark): Int = ticks.compareTo(other.ticks)

    override fun toString(): String = when (ticks) {
        FAR_PAST_TICKS -> "The Far Past"
        FAR_FUTURE_TICKS -> "The Far Future"
        else -> "ServerTimeMark(ticks=$ticks, now=${ServerManager.ticks})"
    }

    companion object {
        val Long.ticks: Duration
            get() = (this * 50).milliseconds
        val Duration.inWholeTicks: Long
            get() = (this.inWholeMilliseconds / 50)

        fun now() = ServerTimeMark(ServerManager.ticks)

        private const val FAR_PAST_TICKS = Long.MIN_VALUE
        private const val FAR_FUTURE_TICKS = Long.MAX_VALUE

        val FAR_PAST = ServerTimeMark(FAR_PAST_TICKS)
        val FAR_FUTURE = ServerTimeMark(FAR_FUTURE_TICKS)
    }
}