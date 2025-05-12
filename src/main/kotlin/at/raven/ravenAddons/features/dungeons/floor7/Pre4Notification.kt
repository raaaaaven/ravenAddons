package at.raven.ravenAddons.features.dungeons.floor7

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.WorldChangeEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.ServerTimeMark
import at.raven.ravenAddons.utils.ServerTimeMark.Companion.inWholeTicks
import at.raven.ravenAddons.utils.SoundUtils
import at.raven.ravenAddons.utils.TimeUtils.formatTicks
import at.raven.ravenAddons.utils.TitleManager
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@LoadModule
object Pre4Notification {

    // REGEX TEST: §aGillsplash§r§a completed a device! (§r§c6§r§a/7)
    private val devicePattern = "^(?<ign>.+) completed a device! (?<number>.+)$".toPattern()

    private val phase3Start = "^\\[BOSS] Goldor: Who dares trespass into my domain\\?".toPattern()

    private var time = ServerTimeMark.FAR_PAST

    private var personalBest = ravenAddonsConfig.pre4PersonalBestNumber
        set(value) {
            ravenAddonsConfig.pre4PersonalBestNumber = value
            ravenAddonsConfig.markDirty()
            field = value
        }

    private val pre4BoundingBox = AxisAlignedBB(
        60.0, 125.0, 32.0, 66.0, 130.0, 38.0
    )

    // TO-DO: Fix Enter Section 4 Title

    /*private val waitingBoundingBox =
        AxisAlignedBB(
            94.0, 133.0, 48.0,
            89.0, 128.0, 43.0
        )


    @SubscribeEvent
    fun onEntityTeleport(event: EntityTeleportEvent) {
        if (event.entity !is EntityPlayer || !event.entity.isRealPlayer()) return
        val distanceToPlayer = event.distanceToPlayer ?: return
        if (distanceToPlayer >= 3.0) return
        val playerPosition = PlayerUtils.getPlayer()?.positionVector ?: return

        if (!SkyBlockIsland.CATACOMBS.isInIsland() || !waitingBoundingBox.isVecInside(playerPosition) || !ravenAddonsConfig.enterSection4Title) return

        ChatUtils.chat("${event.entity.displayName.formattedText.removeColors()}!!")

        TitleManager.setTitle(
            "§aEnter Section 4",
            "${event.entity.displayName.formattedText} §aleaped to you!",
            3.seconds,
            1.seconds,
            1.seconds
        )
    }*/

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!SkyBlockIsland.CATACOMBS.isInIsland()) return

        if (phase3Start.matches(event.cleanMessage)) {
            time = ServerTimeMark.now()
        }

        devicePattern.matchMatcher(event.cleanMessage) {
            val ign = group("ign")
            if (ign != PlayerUtils.playerName) return
            val playerPosition = PlayerUtils.getPlayer()?.positionVector ?: return

            val timeElapsed = time.passedSince().inWholeTicks.toInt()

            if (pre4BoundingBox.isVecInside(playerPosition) && ravenAddonsConfig.pre4Notification) {
                ChatUtils.debug("Pre 4 Notification: Sending title and subtitle for $ign.")

                val title = ravenAddonsConfig.pre4NotificationTitle.replace("&", "§")
                val subtitle = ravenAddonsConfig.pre4NotificationSubtitle.replace("&", "§")

                ravenAddons.runDelayed(5.milliseconds) {
                    TitleManager.setTitle(
                        title, subtitle, 1.5.seconds, 0.seconds, 0.seconds
                    )
                    SoundUtils.pling()
                }

            }

            if (pre4BoundingBox.isVecInside(playerPosition) && ravenAddonsConfig.pre4Announce) {
                ChatUtils.debug("Pre 4 Announce: Sending message in party chat.")

                val message = ravenAddonsConfig.pre4AnnounceMessage.replace("\$time", formatTicks(timeElapsed))

                val announce = if (ravenAddonsConfig.announcePrefix) {
                    "/pc [RA] $message"
                } else {
                    "/pc $message"
                }

                ChatUtils.sendMessage(announce)
            }

            if (pre4BoundingBox.isVecInside(playerPosition) && ravenAddonsConfig.pre4PersonalBest) {
                if (timeElapsed > 600) {
                    ChatUtils.debug("Pre 4 Personal Best: Could not mark pre 4 done so returning.")
                    time = ServerTimeMark.FAR_PAST
                    return@matchMatcher
                }

                // TO-DO: Calculate the difference between new and old pb.
                if (timeElapsed < personalBest) {
                    ChatUtils.chat(
                        "Pre 4 took §f${formatTicks(timeElapsed)}§7. §d§l(NEW PB) §8(Old PB: ${formatTicks(personalBest)})"
                    )
                    personalBest = timeElapsed
                } else {
                    ChatUtils.chat("Pre 4 took §f${formatTicks(timeElapsed)}§7. §8(Old PB: ${formatTicks(personalBest)})")
                }
            }
        }
    }

    /*@SubscribeEvent
    fun onCommand(event: CommandRegistrationEvent) {
        event.register("pre4test") {
            description = "Display Enter Section 4 Title"
            callback = {
                val player = PlayerUtils.getPlayer()
                if (player != null) {
                    EntityTeleportEvent(
                        player.positionVector,
                        player
                    ).post()
                }
            }
        }
    }*/

    @SubscribeEvent
    fun onWorldLoad(event: WorldChangeEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.pre4PersonalBest) return
        time = ServerTimeMark.FAR_PAST
    }
}