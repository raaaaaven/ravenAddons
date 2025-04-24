package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.WorldChangeEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.ServerTimeMark
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.StringUtils.removeColors
import at.raven.ravenAddons.utils.TimeUtils.clampTicks
import at.raven.ravenAddons.utils.TimeUtils.format
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@LoadModule
object LostTimeCalculator {

    private var time = SimpleTimeMark.farPast()

    private var serverTime = ServerTimeMark.FAR_PAST

    private val dungeonStartPattern =
        "^\\[NPC] Mort: Here, I found this map when I first entered the dungeon.".toPattern()

    private val dungeonEndPattern = "\\s+> EXTRA STATS <".toPattern()

    private val kuudraStartPattern = "^\\[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!".toPattern()

    private val kuudraEndPattern = "^\\s+> KUUDRA DOWN!".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!ravenAddonsConfig.lostTimeCalculator) return

        if (dungeonStartPattern.matches(event.message.removeColors()) || kuudraStartPattern.matches(event.message.removeColors())) {
            ChatUtils.debug("Instance Lag Calculator: Starting timer.")
            time = SimpleTimeMark.now()
            serverTime = ServerTimeMark.now()
        }

        if (dungeonEndPattern.matches(event.message.removeColors()) || kuudraEndPattern.matches(event.message.removeColors())) {
            if (time.isFarPast() || serverTime.isFarPast()) {
                ravenAddons.runDelayed(2.5.seconds) {
                    ChatUtils.warning("Could not calculate lost time. Likely cause was you left the instance early.")
                }
                return
            }

            val timeElapsed = time.passedSince().clampTicks()
            val serverTimeElapsed = serverTime.passedSince()

            val lagTimeSeconds = (timeElapsed - serverTimeElapsed).coerceAtLeast(Duration.ZERO)

            ChatUtils.debug("Instance Lag Calculator: Preparing to stop the timer.")

            ravenAddons.runDelayed(2.5.seconds) {
                ChatUtils.chat("Elapsed Time: §f${timeElapsed.format()}§7.")
                ChatUtils.chat("Server Time: §f${serverTimeElapsed.format()}§7.")
                ChatUtils.chat("Approximate Time Lost: §f${lagTimeSeconds.format()}§7.")
            }

            ChatUtils.debug("Lost Time Calculator: Resetting timer.")

            resetTimer()
        }
    }

    private fun resetTimer() {
        time = SimpleTimeMark.farPast()
        serverTime = ServerTimeMark.FAR_PAST
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldChangeEvent) {
        ChatUtils.debug("Lost Time Calculator: World Change Detected! Resetting timer.")
        resetTimer()
    }
}