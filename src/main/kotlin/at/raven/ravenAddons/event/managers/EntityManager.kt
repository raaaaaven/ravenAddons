package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.EntityTeleportEvent
import at.raven.ravenAddons.event.PacketReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EntityUtils
import net.minecraft.network.play.server.S18PacketEntityTeleport
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object EntityManager {
    @SubscribeEvent
    fun onPacket(event: PacketReceivedEvent) {
        if (event.packet is S18PacketEntityTeleport) entityTeleport(event.packet)
    }

    private fun entityTeleport(packet: S18PacketEntityTeleport) {
        val entity = EntityUtils.getEntityByID(packet.entityId) ?: return

        val newPosition = Vec3(
            packet.x.toDouble(),
            packet.y.toDouble(),
            packet.z.toDouble(),
        )

        EntityTeleportEvent(newPosition, entity).post()
    }
}