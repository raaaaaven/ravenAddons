package at.raven.ravenaddons.features

import at.raven.ravenaddons.config.RavenAddonsConfig
import at.raven.ravenaddons.data.HypixelGame
import at.raven.ravenaddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenaddons.loadmodule.LoadModule
import at.raven.ravenaddons.utils.ChatUtils
import at.raven.ravenaddons.utils.RegexUtils.matchMatcher
import at.raven.ravenaddons.utils.TitleManager
import net.minecraftforge.client.event.ClientChatReceivedEvent
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
        "^§r§a§r§6(?<ability>${abilities.joinToString("|")}) §r§ais now available!§r\$".toPattern()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!RavenAddonsConfig.miningAbilityNotification) return

        miningAbilityPattern.matchMatcher(event.message.formattedText) {
            val ability = group("ability")

            ChatUtils.debug("miningAbilityNotification: ready")

            TitleManager.setTitle("§6$ability", "§ais now available!", 3.seconds, 0.5.seconds, 0.5.seconds)
        }
    }
}