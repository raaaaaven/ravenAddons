package at.raven.ravenAddons.features.partyCommands

import at.raven.ravenAddons.config.ravenAddonsConfig.sinceInq
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.StringUtils.removeColors
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object SinceCommand {
    private val sincePattern = "Party > .(?:\\[.*] )?(?<author>\\w+): !since".toPattern()

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
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return

        sincePattern.matchMatcher(event.message.removeColors()) {
            ChatUtils.debug("!since matched. $sinceInq Mobs since.")
            ChatUtils.sendMessage("/pc [RA] $sinceInq Mobs since Inquisitor!")
            return
        }

        inqPattern.matchMatcher(event.message.removeColors()) {
            resetSince()
            return
        }

        if (mobPatterns.any { it.matches(event.message.removeColors()) }) {
            addToSince()
        }

    }

    private fun addToSince() {
        sinceInq += 1
    }

    private fun resetSince() {
        sinceInq = 0
    }
}
