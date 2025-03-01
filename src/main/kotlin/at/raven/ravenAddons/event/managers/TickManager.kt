package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.post
import at.raven.ravenAddons.utils.PlayerUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@LoadModule
object TickManager {
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        if (PlayerUtils.getPlayer() == null) return

        at.raven.ravenAddons.event.TickEvent().post()
    }
}