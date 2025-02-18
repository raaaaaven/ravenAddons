package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.SoundUtils
import at.raven.ravenAddons.utils.StringUtils.removeColors
import at.raven.ravenAddons.utils.TitleManager
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object BetterDeviceNotification {

    // REGEX TEST: §aGillsplash§r§a completed a device! (§r§c6§r§a/7)
    private val devicePattern = "^(?<ign>.+)§r§a completed a device! (?<number>.+)$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!ravenAddonsConfig.betterDeviceNotification) return

        devicePattern.matchMatcher(event.message.removeColors()) {
            val ign = group("ign")
            if (ign != PlayerUtils.playerName) return

            ChatUtils.debug("betterDeviceNotification: attempting to send title and subtitle for $ign")

            ravenAddons.launchCoroutine {
                delay(5)
                TitleManager.setTitle(
                    ravenAddonsConfig.betterDeviceNotificationTitle,
                    ravenAddonsConfig.betterDeviceNotificationSubTitle,
                    1.5.seconds,
                    0.seconds,
                    0.seconds
                )
                SoundUtils.pling()
            }
        }
    }
}