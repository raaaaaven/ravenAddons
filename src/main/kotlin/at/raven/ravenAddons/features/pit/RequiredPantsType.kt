package at.raven.ravenAddons.features.pit

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.TooltipEvent
import at.raven.ravenAddons.event.render.container.ContainerBackgroundDrawEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.InventoryUtils.getAllItems
import at.raven.ravenAddons.utils.InventoryUtils.getContainerName
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.StringUtils.removeColors
import at.raven.ravenAddons.utils.render.GuiRenderUtils.highlight
import gg.essential.universal.ChatColor
import net.minecraft.client.gui.inventory.GuiChest
import net.minecraft.inventory.ContainerChest
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object RequiredPantsType {
    private val colors = listOf(
        "Red" to ChatColor.RED,
        "Yellow" to ChatColor.YELLOW,
        "Blue" to ChatColor.BLUE,
        "Orange" to ChatColor.GOLD,
        "Green" to ChatColor.GREEN,
    )

    private val mysticPattern = "(?:Tier )?(?<tier>I+|Mystic|Fresh)? .+".toPattern()

    @SubscribeEvent
    fun onItemHover(event: TooltipEvent) {
        if (!isEnabled()) return

        val stack = event.itemStack
        val name = stack.displayName.removeColors()

        mysticPattern.matchMatcher(name) {
            val tier = group("tier")
            if (tier == "III") return

            val nonce = stack.getNonce() ?: return
            if (nonce <= 20) return

            val (colorName, colorCode) = colors[nonce % colors.size]

            event.toolTip += listOf("", "§7Requires $colorCode$colorName Pants §7to Tier 3")
        }
    }

    @SubscribeEvent
    fun onContainerBackground(event: ContainerBackgroundDrawEvent) {
        if (!isEnabled() || !ravenAddonsConfig.highlightRequiredPantsType) return
        val gui = event.gui
        if (gui !is GuiChest || gui.getContainerName() != "Mystic Well") return

        val inventory = gui.inventorySlots as ContainerChest

        for ((slot, stack) in inventory.getAllItems()) {
            val name = stack?.displayName?.removeColors() ?: continue

            mysticPattern.matchMatcher(name) {
                val tier = group("tier")
                if (tier == "III") return@matchMatcher

                val nonce = stack.getNonce() ?: return@matchMatcher
                if (nonce <= 20) return@matchMatcher

                val color = colors[nonce % 5].second.color ?: return@matchMatcher
                slot.highlight(color)
            }
        }
    }

    private fun ItemStack.getNonce(): Int? {
        val tag = tagCompound ?: return null
        val extraAttributes = tag.getCompoundTag("ExtraAttributes") ?: return null
        return extraAttributes.getInteger("Nonce")
    }

    private fun isEnabled() = HypixelGame.inPit && ravenAddonsConfig.requiredPantsType
}

