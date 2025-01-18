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
object MiningAbilityNotification {
    private val miningAbilityPattern =
        "^§r§a§r§6(?<ability>[\\w ]+) §r§ais now available!§r\$".toPattern()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!ravenAddonsConfig.miningAbilityNotification) return

        miningAbilityPattern.matchMatcher(event.message.formattedText) {
            val ability = group("ability")

            ChatUtils.debug("miningAbilityNotification: ready")

            TitleManager.setTitle("§6$ability", "§ais now available!", 3.seconds, 0.5.seconds, 0.5.seconds)
        }
    }
}