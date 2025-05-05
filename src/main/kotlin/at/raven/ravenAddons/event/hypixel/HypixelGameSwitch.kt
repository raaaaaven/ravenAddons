package at.raven.ravenAddons.event.hypixel

import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.base.RavenEvent

class HypixelGameSwitch(val oldGame: HypixelGame?, val newGame: HypixelGame?) : RavenEvent()