package at.raven.ravenAddons.event.hypixel

import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.minecraftforge.fml.common.eventhandler.Event
import java.util.UUID

class HypixelPartyEvent(
    inParty: Boolean,
    memberMap: MutableMap<UUID, ClientboundPartyInfoPacket.PartyMember>
) : Event()