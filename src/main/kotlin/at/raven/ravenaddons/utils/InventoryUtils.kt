package at.raven.ravenaddons.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

object InventoryUtils {

    fun ContainerChest.getName() = this.lowerChestInventory.displayName.unformattedText.trim()

    fun getContainerName(): String = Minecraft.getMinecraft().currentScreen.let {
        if (it is GuiChest) {
            it.getContainerName()
        } else ""
    }

    fun GuiChest.getContainerName(): String {
        val container = this.inventorySlots as ContainerChest
        return container.getName()
    }

    fun ContainerChest.getLowerItems(): Map<Slot, ItemStack?> = buildMap {
        for ((slot, stack) in getAllItems()) {
            if (slot.slotNumber == slot.slotIndex) continue
            this[slot] = stack
        }
    }

    fun ContainerChest.getUpperItems(): Map<Slot, ItemStack?> = buildMap {
        for ((slot, stack) in getAllItems()) {
            if (slot.slotNumber != slot.slotIndex) continue
            this[slot] = stack
        }
    }

    fun ContainerChest.getAllItems(): Map<Slot, ItemStack?> = buildMap {
        for (slot in inventorySlots) {
            val stack = slot.stack
            this[slot] = stack
        }
    }
}