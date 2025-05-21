package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.render.RenderOverlayEvent
import me.owdding.ktmodules.Module
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module
object RenderManager {
    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return

        RenderOverlayEvent(event.partialTicks).post()
    }
}