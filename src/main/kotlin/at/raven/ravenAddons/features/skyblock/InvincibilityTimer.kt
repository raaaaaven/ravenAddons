
import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.WorldChangeEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.ServerTimeMark
import at.raven.ravenAddons.utils.TitleManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.regex.Pattern
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@LoadModule
object InvincibilityTimer {

    private var invincibilityActive = false

    private var invincibilityJob: Job? = null

    enum class Invincibility(val pattern: Pattern, val cooldown: Duration) {
        BONZO("^Your (?:. )?Bonzo's Mask saved your life!".toPattern(), 3.seconds),
        PHOENIX("^Your Phoenix Pet saved you from certain death!".toPattern(), 4.seconds),
        SPIRIT("^Second Wind Activated! Your Spirit Mask saved your life!".toPattern(), 3.seconds);

        companion object {
            fun match(message: String): Invincibility? =
                entries.find { it.pattern.matches(message) }
        }
    }

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.invincibilityTimer) return

        val invincibility = Invincibility.match(event.cleanMessage)
        if (invincibility != null) {
            invincibilityActive = true
            invincibilityJob?.cancel()
            invincibilityJob = ravenAddons.launchCoroutine {
                try {
                    val timer = ServerTimeMark.now() + invincibility.cooldown
                    while (timer.isInFuture()) {
                        val timeUntil = timer.timeUntil()
                        val formattedTime = timeUntil.inWholeMilliseconds / 1000f
                        val color = when {
                            timeUntil > 2.seconds -> "§a"
                            timeUntil > 1.seconds -> "§e"
                            else -> "§c"
                        }
                        TitleManager.setTitle(
                            "$color%.3f".format(formattedTime),
                            "",
                            1.seconds,
                            0.seconds,
                            0.seconds
                        )
                        delay(50)
                    }
                } catch (_: CancellationException) {
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldChangeEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.invincibilityTimer || !invincibilityActive) return

        invincibilityJob?.cancel()
        invincibilityActive = false
    }
}
