package at.raven.ravenAddons.data

import at.raven.ravenAddons.event.DebugDataCollectionEvent
import at.raven.ravenAddons.event.hypixel.HypixelGameSwitch
import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import me.owdding.ktmodules.Module
import net.hypixel.data.type.GameType
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

enum class HypixelGame(val gameType: GameType) {
    THE_PIT(GameType.PIT),
    SKYBLOCK(GameType.SKYBLOCK),
    ;

    fun isPlaying(): Boolean = this == currentGame
    fun isNotPlaying(): Boolean = this != currentGame

    @Module
    companion object {
        var currentGame: HypixelGame? = null
            private set

        inline val inSkyBlock: Boolean get() = SKYBLOCK.isPlaying()
        inline val inPit: Boolean get() = THE_PIT.isPlaying()

        fun isPlayingAny(games: Collection<HypixelGame>): Boolean {
            val current = currentGame ?: return false
            return current in games
        }
        fun isPlayingAny(vararg games: HypixelGame): Boolean {
            val current = currentGame ?: return false
            return current in games
        }

        fun isNotPlayingAny(games: Collection<HypixelGame>) = !isPlayingAny(games)
        fun isNotPlayingAny(vararg games: HypixelGame) = !isPlayingAny(*games)

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun onHypixelData(event: HypixelServerChangeEvent) {
            val oldGame = currentGame
            currentGame = entries.find { it.gameType == event.serverType }
            if (oldGame == currentGame) return

            HypixelGameSwitch(oldGame, currentGame).post()
        }

        @SubscribeEvent
        fun onDebug(event: DebugDataCollectionEvent) {
            event.title("HypixelGame")
            if (currentGame == null) {
                event.addIrrelevant("Not playing anything (known)")
            } else {
                event.addData("Playing: $currentGame")
            }
        }
    }
}