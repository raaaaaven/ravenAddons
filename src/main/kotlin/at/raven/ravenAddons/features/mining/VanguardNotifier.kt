package at.raven.ravenAddons.features.mining

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.WorldChangeEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.event.hypixel.IslandChangeEvent
import at.raven.ravenAddons.event.managers.ScoreboardManager
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.SimpleTimeMark
import me.owdding.ktmodules.Module
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Module
object VanguardNotifier {
    // https://regex101.com/r/7bY0CJ/1
    private val playerCreatePartyPattern =
        "^(.*?)\\[RA] Vanguard Found! Type \"!ra join\" to be warped within(.*?)$".toPattern()

    private val playerAttemptJoinPartyPattern =
        "G(?:uild)? > (?:\\[.*] )?(?<author>\\w+)?(?:\\[.*] )?(?:\\s\\[[^]]+])?: !ra join".toPattern()

    // https://regex101.com/r/BzjqgV/1
    private val vanguardRoomIDPattern = "^§.[\\d/]+ §.\\w+ FAIR1$".toPattern()

    private val players = mutableSetOf<String>()
    private var playersFull = false

    private var waitingToWarp = false
    private var timeSincePartyJoin = SimpleTimeMark.farPast()

    private val config get() = ravenAddonsConfig.vanguardNotifierWarpDelay

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.vanguardNotifier) return

        if (playerCreatePartyPattern.matches(event.cleanMessage)) {
            ChatUtils.debug("Vanguard Notifier: Found a previous ravenAddons message in chat.")
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

            ravenAddons.runDelayed(250.milliseconds){
                if (players.size == 3 && !playersFull) {
                    ChatUtils.sendMessage("/gc [RA] Vanguard party is now full. (4/4)")
                    playersFull = true
                }
            }
        }
    }

    @SubscribeEvent
    fun onIslandChange(event: IslandChangeEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.vanguardNotifier) return
        if (event.new != SkyBlockIsland.MINESHAFT) return

        ravenAddons.runDelayed(2.5.seconds) {
            val scoreboard = ScoreboardManager.scoreboardLines

            if (timeSincePartyJoin.passedSince() < 45.seconds) {
                ChatUtils.debug("Vanguard Notifier: ravenAddons message for a Vanguard party was previously matched so returning.")
                return@runDelayed
            }
            if (!scoreboard.any { vanguardRoomIDPattern.matches(it) }) return@runDelayed

            players.clear()
            waitingToWarp = true

            ChatUtils.debug("Vanguard Notifier: Vanguard detected! Message is being sent in guild chat.")

            val message = if (ravenAddonsConfig.vanguardNotifierWarp) {
                "/gc [RA] Vanguard Found! Type \"!ra join\" to be warped within $config seconds."
            } else {
                "/gc [RA] Vanguard Found! Type \"!ra join\" to join the Vanguard party."
            }

            ChatUtils.sendMessage(message)

            if (!ravenAddonsConfig.vanguardNotifierWarp) return@runDelayed

            ravenAddons.runDelayed(config.seconds) {
                waitingToWarp = false

                if (players.isNotEmpty()) {
                    ChatUtils.chat("Warping the party as it has been $config seconds.")
                    ChatUtils.sendMessage("/party warp")
                    
                    if (players.size < 3) {
                        ravenAddons.runDelayed(250.milliseconds) {
                            ChatUtils.sendMessage("/gc [RA] Vanguard expired after $config seconds.")
                        }
                    }
                } else {
                    ChatUtils.chat("Warp was cancelled as no one joined the Vanguard party.")
                    ChatUtils.sendMessage("/gc [RA] Vanguard expired after $config seconds.")
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldLoad(event: WorldChangeEvent) {
        if (!HypixelGame.inSkyBlock || !ravenAddonsConfig.vanguardNotifier) return
        players.clear()
        playersFull = false
        waitingToWarp = false
    }
}