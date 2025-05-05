package at.raven.ravenAddons.event.hypixel

import at.raven.ravenAddons.event.base.RavenEvent
import net.hypixel.data.type.ServerType

class HypixelServerChangeEvent(
    val serverName: String?,
    val serverType: ServerType?,
    val lobbyName: String?,
    val mode: String?,
    val map: String?,
) : RavenEvent()
