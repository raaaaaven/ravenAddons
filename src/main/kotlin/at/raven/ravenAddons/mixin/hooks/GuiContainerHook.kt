package at.raven.ravenAddons.mixin.hooks

import at.raven.ravenAddons.event.render.container.ContainerBackgroundDrawEvent
import at.raven.ravenAddons.event.render.container.ContainerForegroundDrawEvent
import at.raven.ravenAddons.utils.EventUtils.post
import net.minecraft.client.gui.inventory.GuiContainer

class GuiContainerHook(guiAny: Any) {
    val gui: GuiContainer = guiAny as GuiContainer

    fun foregroundDrawn(mouseX: Int, mouseY: Int, partialTicks: Float) {
        ContainerForegroundDrawEvent(gui, gui.inventorySlots, mouseX, mouseY, partialTicks).post()
    }

    fun backgroundDrawn(mouseX: Int, mouseY: Int, partialTicks: Float) {
        ContainerBackgroundDrawEvent(gui, gui.inventorySlots, mouseX, mouseY, partialTicks).post()
    }
}