package at.raven.ravenaddons.data

import at.raven.ravenaddons.event.hypixel.HypixelGameSwitch
import at.raven.ravenaddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenaddons.loadmodule.LoadModule
import at.raven.ravenaddons.utils.EventUtils.post
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
    }
}