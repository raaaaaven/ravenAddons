package at.raven.ravenAddons.utils.render

import at.raven.ravenAddons.config.guieditor.GuiPositionEditorManager
import at.raven.ravenAddons.config.guieditor.data.GuiPosition
import at.raven.ravenAddons.ravenAddons.Companion.mc
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.Slot
import org.lwjgl.opengl.GL11
import java.awt.Color

object GuiRenderUtils {
    val scaledResolution get() = ScaledResolution(mc)
    val scaledWidth get() = scaledResolution.scaledWidth
    val scaledHeight get() = scaledResolution.scaledHeight

    val fontRenderer: FontRenderer get() = mc.fontRendererObj

    fun Slot.highlight(color: Color) {
        highlight(color, xDisplayPosition, yDisplayPosition)
    }

    fun highlight(color: Color, x: Int, y: Int) {
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 0f, 110 + mc.renderItem.zLevel)
        Gui.drawRect(x, y, x + 16, y + 16, color.rgb)
        GlStateManager.popMatrix()
        GlStateManager.enableDepth()
        GlStateManager.enableLighting()
    }

    fun Slot.drawBorder(color: Color) {
        drawBorder(color, xDisplayPosition, yDisplayPosition)
    }

    fun drawBorder(color: Color, x: Int, y: Int) {
        GlStateManager.disableLighting()
        GlStateManager.disableDepth()
        GlStateManager.pushMatrix()
        GlStateManager.translate(0f, 0f, 110 + mc.renderItem.zLevel)
        Gui.drawRect(x, y, x + 1, y + 16, color.rgb)
        Gui.drawRect(x, y, x + 16, y + 1, color.rgb)
        Gui.drawRect(x, y + 15, x + 16, y + 16, color.rgb)
        Gui.drawRect(x + 15, y, x + 16, y + 16, color.rgb)
        GlStateManager.popMatrix()
        GlStateManager.enableDepth()
        GlStateManager.enableLighting()
    }

    fun isPointInRect(
        x: Int,
        y: Int,
        left: Int,
        top: Int,
        width: Int,
        height: Int,
    ): Boolean {
        val inX = x in left..width
        val inY = y in top..height

        return inX && inY
    }

    private fun drawStringCentered(
        str: String?,
        fr: FontRenderer,
        x: Float,
        y: Float,
    ) {
        val strLen = fr.getStringWidth(str)
        val x2 = x - strLen / 2f
        val y2 = y - fr.FONT_HEIGHT / 2f
        GL11.glTranslatef(x2, y2, 0f)
        fr.drawString(str, 0f, 0f, 16777215, true)
        GL11.glTranslatef(-x2, -y2, 0f)
    }

    fun drawStringCentered(
        str: String?,
        x: Int,
        y: Int,
    ) {
        drawStringCentered(str, fontRenderer, x.toFloat(), y.toFloat())
    }

    fun GuiPosition.renderString(
        string: String?,
        label: String,
        yOffset: Int = 0,
        dropShadow: Boolean = true,
    ) {
        if (string.isNullOrEmpty()) return
        val fontRenderer = fontRenderer

        val stringWidth = fontRenderer.getStringWidth(string) * scale
        val stringHeight = 10 * scale

        GuiPositionEditorManager.add(
            this,
            label,
            stringWidth.toInt(),
            stringHeight.toInt(),
        )

        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), (y + yOffset).toFloat(), 0f)
        GlStateManager.scale(scale, scale, scale)
        fontRenderer.drawString(string, 0f, 0f, Color.WHITE.rgb, dropShadow)
        GlStateManager.popMatrix()
    }

    fun GuiPosition.renderStrings(
        strings: List<String>,
        label: String,
        yOffset: Int = 0,
        dropShadow: Boolean = true,
    ) {
        if (strings.isEmpty()) return
        val fontRenderer = fontRenderer

        val maxWidth = fontRenderer.getStringWidth(strings.maxBy { it.length }) * scale
        val stringHeight = 10 * scale
        val totalHeight = stringHeight * strings.size

        GuiPositionEditorManager.add(
            this,
            label,
            maxWidth.toInt(),
            totalHeight.toInt(),
        )

        GlStateManager.pushMatrix()
        GlStateManager.translate(x.toFloat(), (y + yOffset).toFloat(), 0f)
        GlStateManager.scale(scale, scale, scale)
        var top = 0.0
        strings.forEach {
            fontRenderer.drawString(it, 0f, top.toFloat(), Color.WHITE.rgb, dropShadow).also { top += stringHeight }
        }

        GlStateManager.popMatrix()
    }
}
