package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.ConfigFixEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
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

            ChatUtils.debug("Better Device Notification: Sending title and subtitle for $ign.")

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

    private val badConfigLine = "\\t\\t\\[dungeons\\.floor_7\\.better_device_notifications_(?:sub)?title]".toPattern()

    @SubscribeEvent
    fun onConfigFix(event: ConfigFixEvent) {
        event.checkVersion(131) {
            var deleteNext = false
            val newConfig = mutableListOf<String>()

            for (line in event.configLines) {
                if (deleteNext) {
                    deleteNext = false
                    continue
                }

                if (badConfigLine.matches(line)) {
                    deleteNext = true
                } else {
                    newConfig.add(line)
                }
            }

            event.configLines = newConfig
        }
    }
}