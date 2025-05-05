package at.raven.ravenAddons.event.render.container

import at.raven.ravenAddons.event.base.RavenEvent
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container

class ContainerForegroundDrawEvent(
    val gui: GuiContainer,
    val container: Container,
    val mouseX: Int,
    val mouseY: Int,
    val partialTicks: Float,
) : RavenEvent()