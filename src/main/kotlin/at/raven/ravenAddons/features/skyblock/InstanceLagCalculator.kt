package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.event.WorldChangeEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.StringUtils.removeColors
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent

@LoadModule
object InstanceLagCalculator {

    private var time: SimpleTimeMark? = null

    private var serverTicks = 0

    private val dungeonStartPattern = "^\\[NPC] Mort: Here, I found this map when I first entered the dungeon.".toPattern()

    private val kuudraStartPattern = "^\\[NPC] Elle: Okay adventurers, I will go and fish up Kuudra!".toPattern()

    private val endPattern = "\\s+> EXTRA STATS <".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!ravenAddonsConfig.instanceLagCalculator) return

        if (dungeonStartPattern.matches(event.message.removeColors()) || kuudraStartPattern.matches(event.message.removeColors())) {
            ChatUtils.debug("Instance Lag Calculator: Starting timer")
            time = SimpleTimeMark.now()
            serverTicks = 0
        }

        if (endPattern.matches(event.message.removeColors())) {

            if (time == null) {
                ravenAddons.launchCoroutine {
                    delay(2500)
                    ChatUtils.warning("Could not calculate lost time. Likely cause was you left the instance early.")
                }
                return
            }

            val timeElapsed = time?.passedSince() ?: return

            val timeElapsedSeconds = timeElapsed.inWholeMilliseconds / 1000.0
            val tickTimeSeconds = serverTicks / 20.0
            val lagTimeSeconds = timeElapsedSeconds - tickTimeSeconds

            ChatUtils.debug("Instance Lag Calculator: Preparing to stop the timer")

            ravenAddons.launchCoroutine {
                delay(2500)
                ChatUtils.chat("Time Elapsed: ${"%.2f".format(timeElapsedSeconds)}.")
                ChatUtils.chat("Tick Time: ${"%.2f".format(tickTimeSeconds)}.")
                ChatUtils.chat("Time lost to lag: ${"%.2f".format(lagTimeSeconds)}.")
            }


            ChatUtils.debug("Instance Lag Calculator: Resetting timer")

            resetTimer()
        }
    }

    private fun resetTimer() {
        time = null
        serverTicks = 0
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent) {
        if (time != null) {
            serverTicks++
        }
    }

    @SubscribeEvent
    fun WorldChangeEvent(event: WorldChangeEvent) {
        resetTimer()
    }
}