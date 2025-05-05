package at.raven.ravenAddons.event

import at.raven.ravenAddons.event.base.CancellableRavenEvent
import net.minecraft.network.Packet

class PacketReceivedEvent(
    val packet: Packet<*>
) : CancellableRavenEvent()