package at.raven.ravenaddons.mixin.hooks

import at.raven.ravenaddons.event.render.container.ContainerBackgroundDrawEvent
import at.raven.ravenaddons.event.render.container.ContainerForegroundDrawEvent
import at.raven.ravenaddons.utils.EventUtils.post
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