package at.raven.ravenAddons.event.hypixel

import at.raven.ravenAddons.event.base.RavenEvent
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import java.util.UUID

class HypixelPartyEvent(
    val inParty: Boolean,
    val memberMap: MutableMap<UUID, ClientboundPartyInfoPacket.PartyMember>
) : RavenEvent()
