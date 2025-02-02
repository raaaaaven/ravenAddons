package at.raven.ravenAddons.event.render.container

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container
import net.minecraftforge.fml.common.eventhandler.Event

class ContainerBackgroundDrawEvent(
    val gui: GuiContainer,
    val container: Container,
    val mouseX: Int,
    val mouseY: Int,
    val partialTicks: Float,
) : Event()