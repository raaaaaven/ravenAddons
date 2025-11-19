package at.raven.ravenAddons.features.dungeons.floor7

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
import at.raven.ravenAddons.utils.TimeUtils.formatTicks
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object SimonSaysPersonalBestTracker {

    // REGEX TEST: §aGillsplash§r§a completed a device! (§r§c6§r§a/7)
    private val devicePattern = "^(?<ign>.+) completed a device! (?<number>.+)$".toPattern()

    private val phase3Start = "^\\[BOSS] Goldor: Who dares trespass into my domain\\?".toPattern()

    private var time = ServerTimeMark.FAR_PAST

    private var personalBest: Int
        get() = ravenAddons.config.simonSaysPersonalBestNumber
        set(value) {
            ravenAddons.config.simonSaysPersonalBestNumber = value
            ravenAddons.config.save()
        }

    private val boundingBox = AxisAlignedBB(
        110.0, 124.0, 91.0, 107.0, 119.0, 96.0
    )

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!SkyBlockIsland.CATACOMBS.isInIsland() || !ravenAddons.config.simonSaysPersonalBest) return

        if (phase3Start.matches(event.cleanMessage)) {
            time = ServerTimeMark.now()
        }

        devicePattern.matchMatcher(event.cleanMessage) {
            val ign = group("ign")
            if (ign != PlayerUtils.playerName) return
            val playerPosition = PlayerUtils.getPlayer()?.positionVector ?: return

            val timeElapsed = time.passedSince().inWholeTicks.toInt()

            if (boundingBox.isVecInside(playerPosition)) {
                // TO-DO: Calculate the difference between new and old pb.
                if (timeElapsed < personalBest) {
                    ChatUtils.chat(
                        "Simon Says took §f${formatTicks(timeElapsed)}§7. §d§l(NEW PB) §8(Old PB: ${formatTicks(personalBest)})"
                    )
                    personalBest = timeElapsed
                } else {
                    ChatUtils.chat(
                        "Simon Says took §f${formatTicks(timeElapsed)}§7. §8(Old PB: ${formatTicks(personalBest)})"
                    )
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldChangeEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddons.config.simonSaysPersonalBest) return
        time = ServerTimeMark.FAR_PAST
    }
}
