package at.raven.ravenAddons.features.mining

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object GemstonePowderNotification {
    private val gemstonePowderPattern =
        "^\\s+§r§dGemstone Powder §r§8x(?<amount>[\\d,]+)$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!ravenAddonsConfig.gemstonePowderNotification) return

        gemstonePowderPattern.matchMatcher(event.message) {
            val amount = group("amount")
            if (amount.toInt() < ravenAddonsConfig.gemstonePowderThreshold) return

            ChatUtils.debug("Gemstone powder Notification: Found amount: $amount.")
            TitleManager.setTitle("", "§dGemstone Powder §8x$amount", 1.5.seconds, 0.seconds, 0.seconds)
        }
    }
}