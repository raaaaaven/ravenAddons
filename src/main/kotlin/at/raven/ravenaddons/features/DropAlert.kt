package at.raven.ravenaddons.features

import at.raven.ravenaddons.RavenAddons
import at.raven.ravenaddons.config.RavenAddonsConfig
import at.raven.ravenaddons.data.HypixelGame
import at.raven.ravenaddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenaddons.loadmodule.LoadModule
import at.raven.ravenaddons.utils.ChatUtils
import at.raven.ravenaddons.utils.RegexUtils.matchMatcher
import at.raven.ravenaddons.utils.StringUtils.removeColors
import kotlinx.coroutines.delay
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object DropAlert {
    private val rngPattern =
        "^(?<type>PRAY TO RNGESUS|INSANE|CRAZY RARE|VERY RARE|RARE|UNCOMMON|PET) DROP! (?<drop>.+)\$".toPattern()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!RavenAddonsConfig.dropAlert || RavenAddonsConfig.dropAlertUserName.isEmpty()) return

        rngPattern.matchMatcher(event.message.formattedText.removeColors()) {
            val type = group("type")
            val drop = group("drop")

            ChatUtils.debug("dropAlert triggered: $type DROP! $drop")

            RavenAddons.launchCoroutine {
                delay(500)

                ChatUtils.sendMessage("/msg ${RavenAddonsConfig.dropAlertUserName} [RA] $type DROP! $drop")
            }
        }
    }
}