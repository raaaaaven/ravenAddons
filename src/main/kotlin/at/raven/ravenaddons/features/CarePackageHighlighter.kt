package at.raven.ravenaddons.features

import at.raven.ravenaddons.config.RavenAddonsConfig
import at.raven.ravenaddons.data.HypixelGame
import at.raven.ravenaddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenaddons.event.render.container.ContainerBackgroundDrawEvent
import at.raven.ravenaddons.loadmodule.LoadModule
import at.raven.ravenaddons.utils.InventoryUtils.getContainerName
import at.raven.ravenaddons.utils.InventoryUtils.getUpperItems
import at.raven.ravenaddons.utils.RenderUtils.highlight
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
        if (HypixelGame.THE_PIT.isNotPlaying()) return
        if (!RavenAddonsConfig.carePackageHighlighter) return
        if (event.gui !is GuiChest) return
        if (event.gui.getContainerName() != "Chest") return
        val inventory = event.gui.inventorySlots as ContainerChest

        for ((slot, stack) in inventory.getUpperItems()) {
            if (importantItems.any { stack?.displayName?.contains(it) == true }) {
                slot highlight RavenAddonsConfig.carePackageHighlighterColour
            }
        }
    }
}