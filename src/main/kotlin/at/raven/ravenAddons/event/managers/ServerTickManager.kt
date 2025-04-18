package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.PacketReceivedEvent
import at.raven.ravenAddons.event.RealServerTickEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.post
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ServerTickManager {

    var ticks: Long = 0
        private set

    @SubscribeEvent
    fun onPacketRecieved(event: PacketReceivedEvent) {
        if (event.packet !is S32PacketConfirmTransaction) return

        ++ticks
        RealServerTickEvent().post()
    }
}