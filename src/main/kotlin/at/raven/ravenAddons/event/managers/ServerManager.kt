package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.PacketReceivedEvent
import at.raven.ravenAddons.event.RealServerTickEvent
import at.raven.ravenAddons.event.WorldChangeEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.post
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ServerManager {

    @SubscribeEvent
    fun onWorldLoad(event: WorldEvent.Load) {
        WorldChangeEvent().post()
    }

    var ticks: Long = 0
        private set

    @SubscribeEvent
    fun onPacketRecieved(event: PacketReceivedEvent) {
        if (event.packet !is S32PacketConfirmTransaction) return
        val packet = event.packet as? S32PacketConfirmTransaction ?: return
        if (packet.actionNumber > 0) return

        ++ticks
        RealServerTickEvent().post()
    }
}