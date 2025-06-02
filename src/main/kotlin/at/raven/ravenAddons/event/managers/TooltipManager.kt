package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.TooltipEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import net.minecraftforge.event.entity.player.ItemTooltipEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object TooltipManager {
    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        TooltipEvent(event.itemStack, event.toolTip).post()
    }
}