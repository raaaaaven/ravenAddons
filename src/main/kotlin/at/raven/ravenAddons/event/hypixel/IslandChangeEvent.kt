package at.raven.ravenAddons.event.hypixel

import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.base.RavenEvent

class IslandChangeEvent(val new: SkyBlockIsland?, val old: SkyBlockIsland?) : RavenEvent()