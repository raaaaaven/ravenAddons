package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.event.hypixel.HypixelPartyEvent
import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.post
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket
import kotlin.jvm.optionals.getOrNull

@LoadModule
object HypixelEvents {
    private val modApi = HypixelModAPI.getInstance()

    init {
        modApi.subscribeToEventPacket(ClientboundLocationPacket::class.java)
        modApi.createHandler(ClientboundHelloPacket::class.java, ::onHelloPacket)
        modApi.createHandler(ClientboundLocationPacket::class.java, ::onLocationPacket)
        modApi.createHandler(ClientboundPartyInfoPacket::class.java, ::onPartyPacket)
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

    private fun onPartyPacket(packet: ClientboundPartyInfoPacket) {
        HypixelPartyEvent(
            packet.isInParty,
            packet.memberMap
        ).post()
    }

    fun sendPartyPacket() = modApi.sendPacket(ServerboundPartyInfoPacket())
}