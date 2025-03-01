package at.raven.ravenAddons.event.hypixel

import at.raven.ravenAddons.data.HypixelGame
import net.minecraftforge.fml.common.eventhandler.Event

class HypixelGameSwitch(val oldGame: HypixelGame?, val newGame: HypixelGame?) : Event()