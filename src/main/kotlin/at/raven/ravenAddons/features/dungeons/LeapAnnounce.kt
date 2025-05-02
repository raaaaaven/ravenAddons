package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.SoundUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object LeapAnnounce {

    // REGEX TEST: §r§aYou have teleported to §r§bGillsplash§r§a!§r
    private val leapPattern = "^You have teleported to (?<ign>.+)!$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!HypixelGame.inSkyBlock) return

        leapPattern.matchMatcher(event.cleanMessage) {
            val ign = group("ign")

            if (ravenAddonsConfig.leapAnnounce) {
                ChatUtils.debug("Leap Announce: Teleported to $ign.")
                ChatUtils.debug("Leap Announce: Sending ${ravenAddonsConfig.leapAnnounceMessage}.")

                val message = ravenAddonsConfig.leapAnnounceMessage.replace("\$ign", ign)

                val announce = if (ravenAddonsConfig.leapAnnouncePrefix) {
                    "/pc [RA] $message"
                } else {
                    "/pc $message"
                }

                ChatUtils.sendMessage(announce)
            }

            if (ravenAddonsConfig.leapSound) {
                ChatUtils.debug("Leap Sound: Playing SoundUtils.pling().")
                SoundUtils.pling()
            }
        }
    }
}