package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.WorldChangeEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.ServerTimeMark
import at.raven.ravenAddons.utils.SimpleTimeMark
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

    private val kuudraStartPattern = "^\\[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!".toPattern()

    private val endPattern = "^\\s+(?:> EXTRA STATS <|KUUDRA DOWN!)\$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.lostTimeCalculator) return

        if (dungeonStartPattern.matches(event.cleanMessage) || kuudraStartPattern.matches(event.cleanMessage)) {
            ChatUtils.debug("Instance Lag Calculator: Starting timer.")
            time = SimpleTimeMark.now()
            serverTime = ServerTimeMark.now()
        }

        if (endPattern.matches(event.cleanMessage)) {
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
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.lostTimeCalculator) return
        resetTimer()
    }
}