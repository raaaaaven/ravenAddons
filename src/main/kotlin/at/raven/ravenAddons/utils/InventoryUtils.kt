package at.raven.ravenAddons.utils

import net.minecraft.inventory.ContainerChest
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

object InventoryUtils {

    fun ContainerChest.getName() = this.lowerChestInventory.displayName.unformattedText.trim()

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