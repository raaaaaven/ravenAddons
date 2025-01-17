package at.raven.ravenAddons.utils

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@LoadModule
object TitleManager {
    private var title = ""
    private var subTitle = ""

    private var titleTimer = 0
    private var titleDisplayTime = 0

    private var titleFadeIn = 0
    private var titleFadeOut = 0

    fun setTitle(
        title: String? = null,
        subTitle: String? = null,
        timer: Duration,
        fadeIn: Duration,
        fadeOut: Duration,
    ) {
        this.title = title ?: ""
        this.subTitle = subTitle ?: ""

        this.titleTimer = (timer.inSeconds() * 20).toInt()
        this.titleFadeIn = (fadeIn.inSeconds() * 20).toInt()
        this.titleFadeOut = (fadeOut.inSeconds() * 20).toInt()
    }

    private fun command() {
        setTitle(
            "§ahello everyone",
            "§baaa",
            10.seconds,
            1.seconds,
            5.seconds
        )
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Pre) {
        if (event.type != RenderGameOverlayEvent.ElementType.HOTBAR) return

        renderTitle(event.partialTicks)
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("rashowtitle") {
            description = "Display a test title"
            callback { command() }
        }
    }

    private fun renderTitle(partialTicks: Float) {
        val windowWidth = Minecraft.getMinecraft().displayWidth
        val windowHeight = Minecraft.getMinecraft().displayHeight

        val fontRenderer = Minecraft.getMinecraft().fontRendererObj
        var titleProgress = titleTimer - partialTicks
        var alpha = 255

        if (titleTimer > titleFadeOut + titleDisplayTime) {
            var alphaMultiplier = (titleFadeIn + titleDisplayTime + titleFadeOut) - titleProgress
            alpha = (alphaMultiplier.toFloat() * 255f / titleFadeIn.toFloat()).toInt()
        }
        if (titleTimer <= titleFadeOut) {
            var alphaMultiplier = titleProgress
            alpha = (alphaMultiplier.toFloat() * 255f / titleFadeOut.toFloat()).toInt()
        }

        if (alpha.coerceIn(0, 255) > 8) {
            GlStateManager.pushMatrix();
            GlStateManager.translate((windowWidth / 4).toDouble(), (windowHeight / 4).toDouble(), 0.0);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0f, 4.0f, 4.0f);
            val color = alpha shl 24 and 0xFF000000.toInt()
            fontRenderer.drawString(title,
                (-fontRenderer.getStringWidth(title) / 2).toFloat(), -10.0f, 0xFFFFFF or color, true);
            GlStateManager.popMatrix();
            GlStateManager.pushMatrix();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            fontRenderer.drawString(subTitle,
                (-fontRenderer.getStringWidth(subTitle) / 2).toFloat(), 5.0f, 0xFFFFFF or color, true);
            GlStateManager.popMatrix();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.END) return

        if (titleTimer > 0) {
            --titleTimer
            if (titleTimer <= 0) {
                title = ""
                subTitle = ""
            }
        }
    }

    private fun Duration.inSeconds(): Double {
        val millisecondsToSeconds = 1000.0

        return this.inWholeMilliseconds.toDouble() / millisecondsToSeconds
    }
}