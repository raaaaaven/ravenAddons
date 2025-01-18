package at.raven.ravenAddons.features

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object GemstonePowderNotification {
    private val gemstonePowderPattern =
        "^§r\\s+§r§dGemstone Powder §r§8x(?<amount>[\\d,]+)§r$".toPattern()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!ravenAddonsConfig.gemstonePowderNotification) return

        gemstonePowderPattern.matchMatcher(event.message.formattedText) {
            val amount = group("amount")

            ChatUtils.debug("GemstonePowderNotification: $amount")

            TitleManager.setTitle("", "§dGemstone Powder §8x$amount", 1.5.seconds, 0.seconds, 0.seconds)
        }
    }
}