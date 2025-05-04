package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.SoundUtils
import at.raven.ravenAddons.utils.TitleManager
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@LoadModule
object BetterDeviceNotification {

    // REGEX TEST: §aGillsplash§r§a completed a device! (§r§c6§r§a/7)
    private val devicePattern = "^(?<ign>.+) completed a device! (?<number>.+)$".toPattern()

    private val pre4BoundingBox =
        AxisAlignedBB(
            60.0, 125.0, 32.0,
            66.0, 130.0, 38.0
        )

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!SkyBlockIsland.CATACOMBS.isInIsland()) return

        devicePattern.matchMatcher(event.cleanMessage) {
            val ign = group("ign")
            if (ign != PlayerUtils.playerName) return
            val playerPosition = PlayerUtils.getPlayer()?.positionVector ?: return

            if (pre4BoundingBox.isVecInside(playerPosition) && ravenAddonsConfig.pre4Notification) {
                ChatUtils.debug("Pre 4 Notification: Sending title and subtitle for $ign.")

                val title = ravenAddonsConfig.pre4NotificationTitle.replace("&", "§")
                val subtitle = ravenAddonsConfig.pre4NotificationSubtitle.replace("&", "§")

                ravenAddons.runDelayed(5.milliseconds) {
                    TitleManager.setTitle(
                        title,
                        subtitle,
                        1.5.seconds,
                        0.seconds,
                        0.seconds
                    )
                    SoundUtils.pling()
                }

            }

            if (pre4BoundingBox.isVecInside(playerPosition) && ravenAddonsConfig.pre4Announce) {
                ChatUtils.debug("Pre 4 Announce: Sending message in party chat.")

                val message = ravenAddonsConfig.pre4AnnounceMessage

                val announce = if (ravenAddonsConfig.announcePrefix) {
                    "/pc [RA] $message"
                } else {
                    "/pc $message"
                }

                ChatUtils.sendMessage(announce)
            }
        }
    }
}