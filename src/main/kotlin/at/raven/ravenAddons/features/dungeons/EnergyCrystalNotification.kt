package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.TickEvent
import at.raven.ravenAddons.event.render.RenderOverlayEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.render.GuiRenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object EnergyCrystalNotification {
    private const val ENERGY_CRYSTAL = "§cPLACE CRYSTAL"
    private var hasEnergyCrystal = false

    @SubscribeEvent
    fun onTick(event: TickEvent) {
        if (!SkyBlockIsland.CATACOMBS.isInIsland() || !ravenAddons.config.energyCrystalNotification) return

        val player = PlayerUtils.getPlayer() ?: return
        hasEnergyCrystal = player.inventory.mainInventory.any { it != null && it.displayName.contains("Energy Crystal") } == true
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderOverlayEvent) {
        if (!SkyBlockIsland.CATACOMBS.isInIsland() || !ravenAddons.config.energyCrystalNotification) return
        if (!hasEnergyCrystal) return

        val fontRenderer = GuiRenderUtils.fontRenderer
        val x = GuiRenderUtils.scaledWidth / 2
        val y = 40f

        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toDouble(), 0.0, 0.0)
        GlStateManager.scale(2.0f, 2.0f, 2.0f)

        fontRenderer.drawString(
            ENERGY_CRYSTAL,
            (-(fontRenderer.getStringWidth(ENERGY_CRYSTAL) / 2)).toFloat(),
            y,
            0xFFFFFF,
            true
        )

        GlStateManager.popMatrix()
    }
}
