package at.raven.ravenAddons.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.Slot
import java.awt.Color

object RenderUtils {
    infix fun Slot.highlight(color: Color) {
        highlight(color, xDisplayPosition, yDisplayPosition)
    }

    fun highlight(color: Color, x: Int, y: Int) {
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.pushMatrix()
        // TODO don't use z
        GlStateManager.translate(0f, 0f, 110 + Minecraft.getMinecraft().renderItem.zLevel)
        Gui.drawRect(x, y, x + 16, y + 16, color.rgb)
        GlStateManager.popMatrix()
        GlStateManager.enableDepth()
        GlStateManager.enableLighting()
    }

    infix fun Slot.drawBorder(color: Color) {
        drawBorder(color, xDisplayPosition, yDisplayPosition)
    }

    fun drawBorder(color: Color, x: Int, y: Int) {
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 0f, 110 + Minecraft.getMinecraft().renderItem.zLevel)
        Gui.drawRect(x, y, x + 1, y + 16, color.rgb)
        Gui.drawRect(x, y, x + 16, y + 1, color.rgb)
        Gui.drawRect(x, y + 15, x + 16, y + 16, color.rgb)
        Gui.drawRect(x + 15, y, x + 16, y + 16, color.rgb)
        GlStateManager.popMatrix()
        GlStateManager.enableDepth()
        GlStateManager.enableLighting()
    }

    // move to ColorUtils or something better
    fun Color.withAlpha(alpha: Int) = Color(this.red, this.green, this.blue, alpha)
}