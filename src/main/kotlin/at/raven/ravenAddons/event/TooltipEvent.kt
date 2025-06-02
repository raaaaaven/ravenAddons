package at.raven.ravenAddons.event

import at.raven.ravenAddons.event.base.RavenEvent
import net.minecraft.item.ItemStack

class TooltipEvent(val itemStack: ItemStack, private val toolTip0: MutableList<String>) : RavenEvent() {

    var toolTip: MutableList<String>
        set(value) {
            toolTip0.clear()
            toolTip0.addAll(value)
        }
        get() = toolTip0
}
