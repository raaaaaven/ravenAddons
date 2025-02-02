package at.raven.ravenAddons.data

import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenAddons.loadmodule.LoadModule
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

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        fun onHypixelData(event: HypixelServerChangeEvent) {
            currentGame = entries.firstOrNull { it.gameType == event.serverType }
        }
    }
}