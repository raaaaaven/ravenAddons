package at.raven.ravenAddons.features.skyblock

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
object MiningAbilityNotification {

    private val abilities = listOf(
        "Mining Speed Boost",
        "Pickobulus",
        "Anomalous Desire",
        "Maniac Miner",
        "Gemstone Infusion",
        "Sheer Force"
    )

    private val miningAbilityPattern =
        "^§a§r§6(?<ability>${abilities.joinToString("|")}) §r§ais now available!$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!ravenAddonsConfig.miningAbilityNotification) return

        miningAbilityPattern.matchMatcher(event.message) {
            val ability = group("ability")

            ChatUtils.debug("miningAbilityNotification: ready")
            TitleManager.setTitle("§6$ability", "§ais now available!", 3.seconds, 0.5.seconds, 0.5.seconds)
        }
    }
}