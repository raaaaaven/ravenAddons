package at.raven.ravenAddons.features.kuudra

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object KuudraChestLimit {
    private val kuudraCompletePattern = "^\\s+KUUDRA DOWN!$".toPattern()

    private val chestOpenedPattern = "^PAID CHEST REWARDS$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!ravenAddonsConfig.kuudraChestLimitWarning) return

        kuudraCompletePattern.matchMatcher(event.cleanMessage) {
            if (!SkyBlockIsland.KUUDRA.isInIsland()) return
            add()
        }

        chestOpenedPattern.matchMatcher(event.cleanMessage) {
            if (SkyBlockIsland.KUUDRA.isInIsland()) return
            KuudraChestLimit.reset()
        }
    }

    private fun add() {
        ravenAddonsConfig.kuudraChestLimitWarningNumber += 1
        ravenAddonsConfig.markDirty()

        if (ravenAddonsConfig.kuudraChestLimitWarningNumber >= 60) {
            ChatUtils.chat("You've done 60 Kuudra runs without checking your chests.")
            TitleManager.setTitle("§cOpen Chests", "", 3.seconds, 1.seconds, 1.seconds)
        }
    }

    private fun reset() {
        ravenAddonsConfig.kuudraChestLimitWarningNumber += 1
        ravenAddonsConfig.markDirty()
    }
}
