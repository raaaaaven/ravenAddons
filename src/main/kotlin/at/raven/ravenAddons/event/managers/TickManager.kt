package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.utils.PlayerUtils
import me.owdding.ktmodules.Module
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@Module
object TickManager {
    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return
        if (PlayerUtils.getPlayer() == null) return

        at.raven.ravenAddons.event.TickEvent().post()
    }
}