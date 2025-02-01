package at.raven.ravenAddons.features

import at.raven.ravenAddons.event.render.container.ContainerBackgroundDrawEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.InventoryUtils.getUpperItems
import at.raven.ravenAddons.utils.RenderUtils.highlight
import at.raven.ravenAddons.utils.RenderUtils.withAlpha
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

@LoadModule
object CarePackageHighlighter {
    private val importantItems = setOf(
        "Enchanted Book",
        "Diamond",
        "Emerald",
        "Gold Ingot",
        "Iron Ingot",
        "Golden Apple",
        "Notch Apple",
        "Saddle",
        "Name Tag",
        "Horse Armor",
        "Obsidian"
    )

    @SubscribeEvent
    fun onContainerForeground(event: ContainerBackgroundDrawEvent) {
        if (event.gui !is GuiChest) return
        val inventory = event.gui.inventorySlots as ContainerChest

        for ((slot, stack) in inventory.getUpperItems()) {
            if (importantItems.any { stack?.displayName?.contains(it) == true }) {
                slot highlight Color.GREEN.withAlpha(100)
            }
        }
    }
}