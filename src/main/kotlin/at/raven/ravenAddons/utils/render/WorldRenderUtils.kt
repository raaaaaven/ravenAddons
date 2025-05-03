package at.raven.ravenAddons.utils.render

import at.raven.ravenAddons.event.render.WorldRenderEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.sqrt

@LoadModule
object WorldRenderUtils {
    fun WorldRenderEvent.drawString(
        location: Vec3? = null,
        entity: Entity? = null,
        text: String,
        seeThroughBlocks: Boolean = false,
        color: Color? = null,
        partialTicks: Float,
        offset: Vec3? = null,
    ) {
        val viewer = Minecraft.getMinecraft().renderViewEntity ?: return
        GlStateManager.alphaFunc(516, 0.1f)
        GlStateManager.pushMatrix()
        val renderManager = Minecraft.getMinecraft().renderManager

        val interpX = viewer.prevPosX + (viewer.posX - viewer.prevPosX) * partialTicks
        val interpY = viewer.prevPosY + (viewer.posY - viewer.prevPosY) * partialTicks
        val interpZ = viewer.prevPosZ + (viewer.posZ - viewer.prevPosZ) * partialTicks

        var (x, y, z) =
            if (entity != null) {
                val smoothX = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks
                val smoothY = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks
                val smoothZ = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks
                Triple(smoothX - interpX, smoothY - interpY - viewer.eyeHeight + entity.height + 0.5, smoothZ - interpZ)
            } else if (location != null) {
                Triple(location.xCoord - interpX, location.yCoord - interpY - viewer.eyeHeight, location.zCoord - interpZ)
            } else {
                return
            }

        if (offset != null) {
            x += offset.xCoord
            y += offset.yCoord
            z += offset.zCoord
        }

        val distSq = x * x + y * y + z * z
        val dist = sqrt(distSq)
        var modX = x
        var modY = y
        var modZ = z
        if (distSq > 144) {
            modX *= 12 / dist
            modY *= 12 / dist
            modZ *= 12 / dist
        }

        if (seeThroughBlocks) {
            GlStateManager.disableDepth()
            GlStateManager.disableCull()
        }

        GlStateManager.translate(modX, modY, modZ)
        GlStateManager.translate(0f, viewer.eyeHeight, 0f)
        drawNametag(text, color)
        GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
        GlStateManager.translate(0f, -0.25f, 0f)
        GlStateManager.rotate(-renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GlStateManager.popMatrix()
        GlStateManager.disableLighting()

        if (seeThroughBlocks) {
            GlStateManager.enableDepth()
            GlStateManager.enableCull()
        }
    }

    /**
     * @author Mojang
     */
    fun drawNametag(
        str: String,
        color: Color?,
    ) {
        val fontRenderer = Minecraft.getMinecraft().fontRendererObj
        val f1 = 0.02666667f
        GlStateManager.pushMatrix()
        GL11.glNormal3f(0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(-Minecraft.getMinecraft().renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate(
            Minecraft.getMinecraft().renderManager.playerViewX,
            1.0f,
            0.0f,
            0.0f,
        )
        GlStateManager.scale(-f1, -f1, f1)
        GlStateManager.disableLighting()
        GlStateManager.depthMask(false)
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        val tessellator = Tessellator.getInstance()
        val worldrenderer = tessellator.worldRenderer
        val i = 0
        val j = fontRenderer.getStringWidth(str) / 2
        GlStateManager.disableTexture2D()
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR)
        worldrenderer.pos((-j - 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((-j - 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((j + 1).toDouble(), (8 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        worldrenderer.pos((j + 1).toDouble(), (-1 + i).toDouble(), 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex()
        tessellator.draw()
        GlStateManager.enableTexture2D()
        val colorCode = color?.rgb ?: 553648127
        fontRenderer.drawString(str, -j, i, colorCode)
        GlStateManager.depthMask(true)
        fontRenderer.drawString(str, -j, i, -1)
        GlStateManager.enableBlend()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        GlStateManager.popMatrix()
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        if (ravenAddons.mc.fontRendererObj == null) return
        WorldRenderEvent(event.partialTicks).post()
    }
}