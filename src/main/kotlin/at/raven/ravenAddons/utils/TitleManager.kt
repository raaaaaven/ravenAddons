package at.raven.ravenAddons.utils

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.DebugDataCollectionEvent
import at.raven.ravenAddons.event.TickEvent
import at.raven.ravenAddons.event.render.RenderOverlayEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.render.GuiRenderUtils
import net.minecraft.client.renderer.GlStateManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@LoadModule
object TitleManager {
    private var title = ""
    private var subTitle = ""

    private var titleTimer = 0
    private var titleTotalTime = 0
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

        this.titleTotalTime = (timer.inSeconds() * 20).toInt()
        this.titleFadeIn = (fadeIn.inSeconds() * 20).toInt()
        this.titleFadeOut = (fadeOut.inSeconds() * 20).toInt()
    }

    private fun command(args: Array<String>) {
        if (args.size != 3) {
            ChatUtils.warning("Wrong usage! /ratesttitle <duration> <fadeIn> <fadeOut>")
            return
        }
        val title = ravenAddonsConfig.developerTitle.replace("&", "§")
        val subtitle = ravenAddonsConfig.developerSubTitle.replace("&", "§")
        if (title.isEmpty() && subtitle.isEmpty()) {
            ChatUtils.warning("Wrong usage! Either a title or subtitle need to be set in the config.")
            return
        }

        val duration = args.getOrNull(0)?.toIntOrNull()?.seconds
        val fadeIn = args.getOrNull(1)?.toIntOrNull()?.seconds
        val fadeOut = args.getOrNull(2)?.toIntOrNull()?.seconds

        if (duration == null || fadeIn == null || fadeOut == null) {
            ChatUtils.warning("Wrong usage! /ratesttitle <duration> <fadeIn> <fadeOut>")
            return
        }

        setTitle(
            title,
            subtitle,
            duration,
            fadeIn,
            fadeOut
        )
    }

    @SubscribeEvent
    fun onRenderOverlay(event: RenderOverlayEvent) {
        if (titleTotalTime == 0) return
        if (titleTimer >= titleTotalTime) return

        renderTitle(event.partialTicks)
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("ratesttitle") {
            description = "Display a test title."
            callback { command(it) }
        }
    }

    private fun renderTitle(partialTicks: Float) {
        val windowWidth = GuiRenderUtils.scaledWidth
        val windowHeight = GuiRenderUtils.scaledHeight
        val fontRenderer = GuiRenderUtils.fontRenderer

        val fadeOutStart = titleTotalTime - titleFadeOut
        val interpolatedTime = titleTimer + partialTicks

        val alpha = when (interpolatedTime) {
            in 0.0..titleFadeIn.toDouble() -> {
                val alphaMultiplier = interpolatedTime / titleFadeIn
                (alphaMultiplier * 255).toInt()
            }

            in fadeOutStart.toDouble()..titleTotalTime.toDouble() -> {
                val fadeDuration = titleTotalTime - fadeOutStart
                val timeSinceFadeStart = interpolatedTime - fadeOutStart
                val alphaMultiplier = 1.0 - (timeSinceFadeStart / fadeDuration)
                (alphaMultiplier * 255).toInt()
            }

            else -> 255
        }

        if (alpha.coerceIn(0, 255) > 8) {
            GlStateManager.pushMatrix()
            GlStateManager.translate((windowWidth / 2).toDouble(), (windowHeight / 2).toDouble(), 0.0)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            GlStateManager.pushMatrix()
            GlStateManager.scale(4.0f, 4.0f, 4.0f)
            val color = alpha shl 24 and 0xFF000000.toInt()
            fontRenderer.drawString(
                title,
                (-fontRenderer.getStringWidth(title) / 2).toFloat(), -10.0f, 0xFFFFFF or color, true
            )
            GlStateManager.popMatrix()
            GlStateManager.pushMatrix()
            GlStateManager.scale(2.0f, 2.0f, 2.0f)
            fontRenderer.drawString(
                subTitle,
                (-fontRenderer.getStringWidth(subTitle) / 2).toFloat(), 5.0f, 0xFFFFFF or color, true
            )
            GlStateManager.popMatrix()
            GlStateManager.disableBlend()
            GlStateManager.popMatrix()
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent) {
        if (titleTimer < titleTotalTime) {
            ++titleTimer
            if (titleTotalTime <= titleTimer) {
                title = ""
                subTitle = ""
                titleTotalTime = 0
                titleTimer = 0
            }
        }
    }

    @SubscribeEvent
    fun onDebug(event: DebugDataCollectionEvent) {
        event.title("Title Manager")
        if (titleTimer == 0) {
            event.addIrrelevant("Not displaying anything")
            return
        }
        event.addData {
            add("title: '$title'")
            add("subtitle: '$subTitle'")
            add("")
            add("totalTime = $titleTotalTime")
            add("remainingTime = $titleTimer")
            add("fadeIn = $titleFadeIn")
            add("fadeOut = $titleFadeOut")
        }
    }

    private fun Duration.inSeconds(): Double = this.inWholeMilliseconds.toDouble() / 1000.0
}