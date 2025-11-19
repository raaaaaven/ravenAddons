package at.raven.ravenAddons.features.mining

import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.data.SkyBlockIsland.Companion.miningIslands
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.seconds

@LoadModule
object GemstonePowderNotification {
    private val gemstonePowderPattern =
        "^\\s+§r§dGemstone Powder §r§8x(?<amount>[\\d,]+)$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!SkyBlockIsland.inAnyIsland(miningIslands) || !ravenAddons.config.gemstonePowderNotification) return

        gemstonePowderPattern.matchMatcher(event.message) {
            val amount = group("amount")
            val powder = amount.replace(",", "")
            val convertedAmount = powder.toIntOrNull() ?: return@matchMatcher

            if (convertedAmount < ravenAddons.config.gemstonePowderThreshold) return

            ChatUtils.debug("Gemstone powder Notification: Found amount: $amount.")
            TitleManager.setTitle("", "§dGemstone Powder §8x$amount", 1.5.seconds, ZERO, ZERO)
        }
    }
}
