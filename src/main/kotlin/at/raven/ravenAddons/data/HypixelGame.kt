package at.raven.ravenAddons.data

import at.raven.ravenAddons.event.DebugDataCollectionEvent
import at.raven.ravenAddons.event.hypixel.HypixelGameSwitch
import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.post
import net.hypixel.data.type.GameType
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

enum class HypixelGame(val gameType: GameType) {
    THE_PIT(GameType.PIT),
    SKYBLOCK(GameType.SKYBLOCK),
    ;

    @LoadModule
    companion object {
        var currentGame: HypixelGame? = null
            private set
        fun HypixelGame.isPlaying() = this == currentGame
        fun Collection<HypixelGame>.isPlayingAny() = this.any { it.isPlaying() }

        fun HypixelGame.isNotPlaying() = this != currentGame
        fun Collection<HypixelGame>.isNotPlayingAny() = this.any { it.isNotPlaying() }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun onHypixelData(event: HypixelServerChangeEvent) {
            val oldGame = currentGame
            currentGame = entries.firstOrNull { it.gameType == event.serverType }
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