package at.raven.ravenAddons.features

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import kotlinx.coroutines.delay
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object DropAlert {
    private val rngPattern =
        "^(?<type>PRAY TO RNGESUS|INSANE|CRAZY RARE|VERY RARE|RARE|UNCOMMON|PET) DROP! (?<drop>.+)\$".toPattern()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!ravenAddonsConfig.dropAlert || ravenAddonsConfig.dropAlertUserName.isEmpty()) return

        rngPattern.matchMatcher(event.message.formattedText) {
            val type = group("type")
            val drop = group("drop")

            ChatUtils.debug("magicFindAlert triggered: $type DROP! $drop")

            ravenAddons.launchCoroutine {
                // this delay is probably unneeded
                delay(500) // it makes it run in a different thread so the game doesn't freeze for 500ms

                ChatUtils.sendMessage("/msg ${ravenAddonsConfig.dropAlertUserName} [RA] $type DROP! $drop")
            }
        }
    }
}