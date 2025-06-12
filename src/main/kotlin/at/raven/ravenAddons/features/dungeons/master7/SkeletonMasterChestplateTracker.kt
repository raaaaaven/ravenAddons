package at.raven.ravenAddons.features.dungeons.master7

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.event.managers.ScoreboardManager
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object SkeletonMasterChestplateTracker {
    // https://regex101.com/r/TDrnhE/2
    private val chestplatePattern = "^(?:\\[[MVIP+]+] )?(?<username>\\w+) has obtained (?:\\w+) Skeleton Master Chestplate!\$".toPattern()

    private val m7RoomIDPattern = "^ §7⏣ §cThe Catacombs §7(M7)".toPattern()

    private val dungeonPattern = "^\\[NPC] Mort: Here, I found this map when I first entered the dungeon.".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!SkyBlockIsland.CATACOMBS.isInIsland() || !ravenAddonsConfig.skeletonMasterChestplateTracker) return

        dungeonPattern.matchMatcher(event.cleanMessage) {
            ravenAddons.runDelayed(5.seconds) {
                val scoreboard = ScoreboardManager.scoreboardLines

                if (!scoreboard.any { m7RoomIDPattern.matches(it) }) return@runDelayed

                ravenAddonsConfig.skeletonMasterChestplateTrackerNumber += 1
                ravenAddonsConfig.markDirty()
            }
        }

        chestplatePattern.matchMatcher(event.cleanMessage) {
            val username = group("username")
            if (username != PlayerUtils.playerName) return

            TitleManager.setTitle(
                "§6Skeleton Master Chestplate",
                "§7It took you &f${ravenAddonsConfig.skeletonMasterChestplateTrackerNumber} M7 §7runs",
                3.seconds,
                1.seconds,
                1.seconds,
            )
            ChatUtils.chat(
                "It took you §f${ravenAddonsConfig.skeletonMasterChestplateTrackerNumber}s M7 runs " +
                    "§7since your last Skeleton Master Chestplate.",
            )
            ravenAddonsConfig.skeletonMasterChestplateTrackerNumber = 0
            ravenAddonsConfig.markDirty()
        }
    }
}
