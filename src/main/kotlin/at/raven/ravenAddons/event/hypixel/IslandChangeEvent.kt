package at.raven.ravenAddons.event.hypixel

import at.raven.ravenAddons.data.SkyBlockIsland
import net.minecraftforge.fml.common.eventhandler.Event

class IslandChangeEvent(val new: SkyBlockIsland?, val old: SkyBlockIsland?) : Event()