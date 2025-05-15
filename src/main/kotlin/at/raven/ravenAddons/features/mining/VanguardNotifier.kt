package at.raven.ravenAddons.features.mining

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.data.SkyBlockIsland.Companion.miningIslands
import at.raven.ravenAddons.event.ScoreboardUpdateEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
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
    private val guild = "\\[RA] Vanguard Found! Type \"/ra join\" to get warped.".toPattern()

    private val join =
        "§2G(?:uild)? > §.(?:\\[.*?])? (?<author>\\w+)(?: §2\\[.*?])?§f: (?:(?:§r)?)+!ra join?".toPattern()

    // https://regex101.com/r/BzjqgV/1
    private val vanguard = "^§.[\\d\\/]+ §.[\\w]+ FAIR1\$".toPattern()

    private val players = mutableSetOf<String>()

    private var warp = false

    private var guildTime =  SimpleTimeMark.farPast()

    private var seconds = ravenAddonsConfig.vanguardNotifierWarpDelay.seconds

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!SkyBlockIsland.inAnyIsland(miningIslands) || !ravenAddonsConfig.vanguardNotifier) return

        if (guild.matches(event.cleanMessage)) {
            ChatUtils.debug("Vanguard Notifier: Found [RA] message in chat.")
            guildTime = SimpleTimeMark.now()
        }

        join.matchMatcher(event.message) {
            val player = group("author")

            if (players.size >= 3) {
                ChatUtils.debug("Vanguard Notifier: Three invites have already been sent out.")
                return
            }

            players.add(player)
            ChatUtils.chat("Attempting to invite $player to the Vanguard party.")
            ChatUtils.sendMessage("/p invite $player")
        }
    }

    @SubscribeEvent
    fun onScoreboard(event: ScoreboardUpdateEvent) {
        if (!SkyBlockIsland.inAnyIsland(miningIslands) || !ravenAddonsConfig.vanguardNotifier) return
        if (warp) return

        val scoreboard = ScoreboardManager.scoreboardLines

        if (scoreboard.any { vanguard.matches(it) }) {
            if (guildTime.passedSince().inWholeSeconds < 30) return

            players.clear()
            warp = false

            ChatUtils.sendMessage("/gc [RA] Vanguard Found! Type \"/ra join\" to be warped within 20 seconds.")
        }

        ravenAddons.runDelayed(seconds) {
            warp = true

            if (players.isNotEmpty()) {
                ChatUtils.chat("Warping the party as it has been $seconds seconds since you have entered.")
                ChatUtils.sendMessage("/party warp")
            } else {
                ChatUtils.debug("Vanguard Notifier: Player size is 0 so do not warp.")
            }
        }
    }
}