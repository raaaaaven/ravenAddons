package at.raven.ravenAddons.features.partyCommands

import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object SinceCommand {
    // https://regex101.com/r/DYjrgE/1
    private val sincePattern = "^Party > (?:\\[.*] )?(?<author>([^:]*)?): !since".toPattern()

    private val inqPattern = ".* You dug out a Minos Inquisitor!".toPattern()

    private val mobPatterns = listOf(
        ".* You dug out a Gaia Construct!".toPattern(),
        ".* You dug out a Minos Champion!".toPattern(),
        ".* You dug out a Minos Hunter!".toPattern(),
        ".* You dug out a Minotaur!".toPattern(),
        ".* You dug out Siamese Lynxes!".toPattern(),
    )

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!HypixelGame.inSkyBlock) return

        sincePattern.matchMatcher(event.cleanMessage) {
            if (!ravenAddons.config.sinceCommand) return
            ravenAddons.launchCoroutine {
                delay(250)
                ChatUtils.debug("Since Command: ${ravenAddons.config.sinceInq} Mobs since.")
                ChatUtils.sendMessage("/pc [RA] ${ravenAddons.config.sinceInq} Mobs since Inquisitor!")
            }
            return
        }

        inqPattern.matchMatcher(event.cleanMessage) {
            ChatUtils.debug("Since Command: Inquisitor detected. Resetting sinceInq counter.")
            resetSince()
            return
        }

        if (mobPatterns.any { it.matches(event.cleanMessage) }) {
            ChatUtils.debug("Since Command: Mythologicial Mob detected. Adding to sinceInq counter.")
            addToSince()
        }

    }

    private fun addToSince() {
        ravenAddons.config.sinceInq += 1
    }

    private fun resetSince() {
        ravenAddons.config.sinceInq = 0
    }
}
