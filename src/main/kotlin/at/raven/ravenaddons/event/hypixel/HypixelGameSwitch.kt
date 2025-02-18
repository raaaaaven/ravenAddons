package at.raven.ravenaddons.event.hypixel

import at.raven.ravenaddons.data.HypixelGame
import net.minecraftforge.fml.common.eventhandler.Event

class HypixelGameSwitch(val oldGame: HypixelGame?, val newGame: HypixelGame?): Event()