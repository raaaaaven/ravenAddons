package at.raven.ravenAddons.features.mining

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.data.SkyBlockIsland.Companion.miningIslands
import at.raven.ravenAddons.event.WorldChangeEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.event.hypixel.IslandChangeEvent
import at.raven.ravenAddons.event.managers.ScoreboardManager
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.SimpleTimeMark
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object VanguardNotifier {
    private val playerCreatePartyPattern = ".*\\[RA] Vanguard Found! Type \"/ra join\" to get warped.".toPattern()

    private val playerAttemptJoinPartyPattern = "G(?:uild)? > (?:\\[.*] )?(?<author>\\w+)?(?:\\[.*] )?(?:\\s\\[[^]]+])?: !ra join".toPattern()

    // https://regex101.com/r/BzjqgV/1
    private val vanguardRoomIDPattern = "^§.[\\d/]+ §.\\w+ FAIR1$".toPattern()

    private val players = mutableSetOf<String>()

    private var waitingToWarp = false
    private var timeSincePartyJoin = SimpleTimeMark.farPast()

    private val config get() = ravenAddonsConfig.vanguardNotifierWarpDelay

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!SkyBlockIsland.inAnyIsland(miningIslands) || !ravenAddonsConfig.vanguardNotifier) return

        if (playerCreatePartyPattern.matches(event.cleanMessage)) {
            ChatUtils.debug("Vanguard Notifier: Found [RA] message in chat.")
            timeSincePartyJoin = SimpleTimeMark.now()
        }

        playerAttemptJoinPartyPattern.matchMatcher(event.cleanMessage) {
            if (!waitingToWarp) return@matchMatcher
            val player = group("author")

            if (players.size >= 3) {
                ChatUtils.debug("Vanguard Notifier: Three invites have already been sent out.")
                return
            }

            players.add(player)
            ChatUtils.chat("Inviting $player to the Vanguard party.")
            ChatUtils.sendMessage("/p invite $player")
        }
    }

    @SubscribeEvent
    fun onIslandChange(event: IslandChangeEvent) {
        if (event.new != SkyBlockIsland.MINESHAFT) return

        ravenAddons.runDelayed(2.5.seconds) {
            val scoreboard = ScoreboardManager.scoreboardLines

            if (timeSincePartyJoin.passedSince() < 30.seconds) return@runDelayed
            if (!scoreboard.any { vanguardRoomIDPattern.matches(it) }) return@runDelayed

            players.clear()
            waitingToWarp = true

            ChatUtils.sendMessage("/gc [RA] Vanguard Found! Type \"!ra join\" to be warped within $config seconds.")

            ravenAddons.runDelayed(config.seconds) {
                waitingToWarp = false

                if (players.isNotEmpty()) {
                    ChatUtils.chat("Warping the party as it has been $config seconds since you have entered.")
                    ChatUtils.sendMessage("/party warp")
                } else {
                    ChatUtils.chat("Cancelling the warp as it has been $config seconds and nobody has joined.")
                    ChatUtils.debug("Vanguard Notifier: Player size is 0 so do not warp.")
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldChangeEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.vanguardNotifier) return
        players.clear()
        waitingToWarp = false
        timeSincePartyJoin = SimpleTimeMark.farPast()
    }
}