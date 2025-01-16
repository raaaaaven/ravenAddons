package at.raven.ravenAddons.event.hypixel

import net.hypixel.data.type.ServerType
import net.minecraftforge.fml.common.eventhandler.Event

class HypixelServerChangeEvent(
    val serverName: String?,
    val serverType: ServerType?,
    val lobbyName: String?,
    val mode: String?,
    val map: String?,
) : Event()
