package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.utils.RegexUtils.matches
import me.owdding.ktmodules.Module
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module
object BlazetekkHamRadioMessageHider {

    private val radioPatterns = listOf(
        "^Your radio is weak. Find another enjoyer to boost it.".toPattern(),
        "^Your radio signal is strong!".toPattern(),
        "^Your radio lost signal. There's too many enjoyers on this channel.".toPattern()
    )

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.blazetekkHamRadioMessageHider) return

        if (radioPatterns.any { it.matches(event.cleanMessage) }) {
            event.isCanceled = true
        }
    }
}