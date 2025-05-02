package at.raven.ravenAddons.features.pit

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.render.container.ContainerBackgroundDrawEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.InventoryUtils.getContainerName
import at.raven.ravenAddons.utils.InventoryUtils.getUpperItems
import at.raven.ravenAddons.utils.render.GuiRenderUtils.highlight
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
    fun onContainerBackground(event: ContainerBackgroundDrawEvent) {
        if (!HypixelGame.inPit) return
        if (!ravenAddonsConfig.carePackageHighlighter) return
        if (event.gui !is GuiChest) return
        if (event.gui.getContainerName() != "Chest") return
        val inventory = event.gui.inventorySlots as ContainerChest

        for ((slot, stack) in inventory.getUpperItems()) {
            if (importantItems.any { stack?.displayName?.contains(it) == true }) {
                slot.highlight(ravenAddonsConfig.carePackageHighlighterColour)
            }
        }
    }
}