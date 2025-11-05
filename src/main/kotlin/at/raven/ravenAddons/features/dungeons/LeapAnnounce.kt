package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
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
        if (!SkyBlockIsland.CATACOMBS.isInIsland()) return

        leapPattern.matchMatcher(event.cleanMessage) {
            val ign = group("ign")

            if (ravenAddons.config.leapAnnounce) {
                ChatUtils.debug("Leap Announce: Teleported to $ign.")
                ChatUtils.debug("Leap Announce: Sending ${ravenAddons.config.leapAnnounceMessage}.")

                val message = ravenAddons.config.leapAnnounceMessage.replace("\$ign", ign)

                val announce = if (ravenAddons.config.announcePrefix) {
                    "/pc [RA] $message"
                } else {
                    "/pc $message"
                }

                ChatUtils.sendMessage(announce)
            }

            if (ravenAddons.config.leapSound) {
                ChatUtils.debug("Leap Sound: Playing SoundUtils.pling().")
                SoundUtils.pling()
            }
        }
    }
}
