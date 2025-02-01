package at.raven.ravenAddons.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

object InventoryUtils {

    fun ContainerChest.getName() = this.lowerChestInventory.displayName.unformattedText.trim()

    fun getContainerName(): String = Minecraft.getMinecraft().currentScreen.let {
        if (it is GuiChest) {
            val chest = it.inventorySlots as ContainerChest
            chest.getName()
        } else ""
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