package at.raven.ravenAddons.features

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.event.render.container.ContainerBackgroundDrawEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.InventoryUtils
import at.raven.ravenAddons.utils.InventoryUtils.getUpperItems
import at.raven.ravenAddons.utils.RenderUtils.highlight
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object CarePackageHighlighter {
    private val importantItems = setOf(
        "Mystic Sword",
        "Mystic Bow",
        "Fresh Red Pants",
        "Fresh Orange Pants",
        "Fresh Yellow Pants",
        "Fresh Green Pants",
        "Fresh Blue Pants"
    )

    @SubscribeEvent
    fun onContainerForeground(event: ContainerBackgroundDrawEvent) {
        if (!ravenAddonsConfig.carePackageHighlighter) return
        if (InventoryUtils.getContainerName() != "Chest") return
        if (event.gui !is GuiChest) return
        val inventory = event.gui.inventorySlots as ContainerChest

        for ((slot, stack) in inventory.getUpperItems()) {
            if (importantItems.any { stack?.displayName?.contains(it) == true }) {
                slot highlight ravenAddonsConfig.carePackageHighlighterColour
            }
        }
    }
}