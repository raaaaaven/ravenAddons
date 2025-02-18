package at.raven.ravenaddons.features

import at.raven.ravenaddons.config.RavenAddonsConfig
import at.raven.ravenaddons.data.HypixelGame
import at.raven.ravenaddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenaddons.loadmodule.LoadModule
import at.raven.ravenaddons.utils.ChatUtils
import at.raven.ravenaddons.utils.PlayerUtils
import at.raven.ravenaddons.utils.RegexUtils.matchMatcher
import at.raven.ravenaddons.utils.SoundUtils
import at.raven.ravenaddons.utils.StringUtils.removeColors
import at.raven.ravenaddons.utils.TitleManager
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object BetterDeviceNotification {

    // REGEX TEST: §r§r§r§aGillsplash§r§a completed a device! (§r§c6§r§a/7)§r
    private val devicePattern = "^§r§r§r(?<ign>.+)§r§a completed a device! (?<number>.+)\$".toPattern()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!RavenAddonsConfig.betterDeviceNotification) return

        devicePattern.matchMatcher(event.message.formattedText.removeColors()) {
            val ign = group("ign")
            val numbers = group("numbers")

            if (ign != PlayerUtils.playerName) return

            ChatUtils.debug("betterDeviceNotification: attempting to send title and subtitle for $ign")

            TitleManager.setTitle(
                "${RavenAddonsConfig.betterDeviceNotificationTitle}",
                "${RavenAddonsConfig.betterDeviceNotificationSubTitle}",
                1.5.seconds,
                0.seconds,
                0.seconds)
            SoundUtils.pling()
        }
    }
}