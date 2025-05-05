package at.raven.ravenAddons.event.hypixel

import at.raven.ravenAddons.event.base.RavenEvent
import net.hypixel.data.region.Environment

class HypixelJoinEvent(
    val type: Environment,
) : RavenEvent()
