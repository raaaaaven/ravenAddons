package at.raven.ravenAddons.features.mining

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.data.SkyBlockIsland.Companion.miningIslands
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

    // REGEX TEST: https://regex101.com/r/puEFul/1
    private val miningAbilityPattern =
        "^§a§r§6(?<ability>${abilities.joinToString("|")}) §r§ais now available!$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!SkyBlockIsland.inAnyIsland(miningIslands) || !ravenAddonsConfig.miningAbilityNotification) return

        miningAbilityPattern.matchMatcher(event.message) {
            val ability = group("ability")

            ChatUtils.debug("Mining Ability Notification: Mining ability is ready.")
            TitleManager.setVanillaTitle("§6$ability", "§ais now available!", 60, 10, 10)
        }
    }
}
