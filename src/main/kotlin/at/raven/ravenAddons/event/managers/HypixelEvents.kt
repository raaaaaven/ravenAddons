package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.post
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import kotlin.jvm.optionals.getOrNull

@LoadModule
object HypixelEvents {
    init {
        val modApi = HypixelModAPI.getInstance()
        modApi.subscribeToEventPacket(ClientboundLocationPacket::class.java)
        modApi.createHandler(ClientboundHelloPacket::class.java, ::onHelloPacket)
        modApi.createHandler(ClientboundLocationPacket::class.java, ::onLocationPacket)
    }

    private fun onHelloPacket(packet: ClientboundHelloPacket) {
        HypixelJoinEvent(packet.environment).post()
    }

    private fun onLocationPacket(packet: ClientboundLocationPacket) {
        HypixelServerChangeEvent(
            packet.serverName,
            packet.serverType.getOrNull(),
            packet.lobbyName.getOrNull(),
            packet.mode.getOrNull(),
            packet.map.getOrNull(),
        ).post()
    }
}