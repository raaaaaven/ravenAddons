package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.render.RenderOverlayEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object RenderManager {
    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return

        RenderOverlayEvent(event.partialTicks).post()
    }
}