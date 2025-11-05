package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.RegexUtils.matches
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object BlazetekkHamRadioMessageHider {

    private val radioPatterns = listOf(
        "^Your radio is weak. Find another enjoyer to boost it.".toPattern(),
        "^Your radio signal is strong!".toPattern(),
        "^Your radio lost signal. There's too many enjoyers on this channel.".toPattern()
    )

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddons.config.blazetekkHamRadioMessageHider) return

        if (radioPatterns.any { it.matches(event.cleanMessage) }) {
            event.isCanceled = true
        }
    }
}
