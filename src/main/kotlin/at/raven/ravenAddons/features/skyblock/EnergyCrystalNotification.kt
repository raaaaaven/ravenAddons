package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.PlayerUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@LoadModule
object EnergyCrystalNotification {
    private const val ENERGY_CRYSTAL = "§cPLACE CRYSTAL"
    private var hasEnergyCrystal = false
    private val mc get() = Minecraft.getMinecraft()

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
//        if (!ravenAddonsConfig.energyCrystalNotification) return

        val player = PlayerUtils.getPlayer() ?: return
        hasEnergyCrystal = player.inventory.mainInventory.any { it != null && it.displayName.contains("Energy Crystal") } == true
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
//        if (!ravenAddonsConfig.energyCrystalNotification) return
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return
        if (!hasEnergyCrystal) return

        val fontRenderer = mc.fontRendererObj
        val x = ScaledResolution(mc).scaledWidth / 2
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