package at.raven.ravenAddons.utils

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.commands.CommandCategory
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.ConfigFixEvent
import at.raven.ravenAddons.event.DebugDataCollectionEvent
import at.raven.ravenAddons.event.PacketReceivedEvent
import at.raven.ravenAddons.event.render.RenderOverlayEvent
import at.raven.ravenAddons.event.render.TitleReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.cancel
import at.raven.ravenAddons.utils.EventUtils.post
import at.raven.ravenAddons.utils.StringUtils.cleanupColors
import at.raven.ravenAddons.utils.render.GuiRenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.server.S45PacketTitle
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@LoadModule
object TitleManager {
    private val titlesToRender = mutableListOf<TitleObject>()

    fun setTitle(
        title: String? = null,
        subTitle: String? = null,
        timer: Duration,
        fadeIn: Duration,
        fadeOut: Duration,
    ) {
        val title = TitleObject(
            title ?: "",
            subTitle ?: "",
            timer,
            fadeIn,
            fadeOut
        )

        setTitle(title)
    }

    fun setTitle(title: TitleObject) {
        clearVanillaTitle()

        titlesToRender.clear()
        titlesToRender.add(title)
    }

    fun addTitle(
        title: String? = null,
        subTitle: String? = null,
        timer: Duration,
        fadeIn: Duration,
        fadeOut: Duration,
    ) {
        val title = TitleObject(
            title ?: "",
            subTitle ?: "",
            timer,
            fadeIn,
            fadeOut
        )

        addTitle(title)
    }

    fun addTitle(title: TitleObject) {
        clearVanillaTitle()

        titlesToRender.add(title)
    }

    private fun command(args: Array<String>) {
        if (args.size != 3) {
            ChatUtils.warning("Wrong usage! /ra testtitle <duration> <fadeIn> <fadeOut>")
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
            ChatUtils.warning("Wrong usage! /ra testtitle <duration> <fadeIn> <fadeOut>")
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
        val iterator = titlesToRender.iterator()
        while (iterator.hasNext()) {
            val title = iterator.next()

            if (title.isExpired()) {
                iterator.remove()
            } else {
                renderTitle(title, event.partialTicks)
            }
        }
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("testtitle") {
            description = "Display a test title."
            category = CommandCategory.DEVELOPER
            callback { command(it) }
        }
    }

    private fun renderTitle(title: TitleObject, partialTicks: Float) {
        val windowWidth = GuiRenderUtils.scaledWidth
        val windowHeight = GuiRenderUtils.scaledHeight
        val fontRenderer = GuiRenderUtils.fontRenderer

        val alpha = title.getAlpha(partialTicks)

        if (alpha.coerceIn(0, 255) > 8) {
            GlStateManager.pushMatrix()
            GlStateManager.translate((windowWidth / 2).toDouble(), (windowHeight / 2).toDouble(), 0.0)
            GlStateManager.enableBlend()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            GlStateManager.pushMatrix()
            GlStateManager.scale(4.0f, 4.0f, 4.0f)
            val color = alpha shl 24 and 0xFF000000.toInt()
            fontRenderer.drawString(
                title.titleText,
                (-fontRenderer.getStringWidth(title.titleText) / 2).toFloat(), -10.0f, 0xFFFFFF or color, true
            )
            GlStateManager.popMatrix()
            GlStateManager.pushMatrix()
            GlStateManager.scale(2.0f, 2.0f, 2.0f)
            fontRenderer.drawString(
                title.subTitleText,
                (-fontRenderer.getStringWidth(title.subTitleText) / 2).toFloat(), 5.0f, 0xFFFFFF or color, true
            )
            GlStateManager.popMatrix()
            GlStateManager.disableBlend()
            GlStateManager.popMatrix()
        }
    }

    @SubscribeEvent
    fun onDebug(event: DebugDataCollectionEvent) {
        event.title("Title Manager")
        if (titlesToRender.isEmpty()) {
            event.addIrrelevant("Not displaying anything")
            return
        }
        event.addData {
            titlesToRender.forEach {
                add("--")
                add("title: '${it.titleText}'")
                add("subtitle: '${it.subTitleText}'")
                add("")
                add("duration = ${it.titleDuration}")
                add("fadeIn = ${it.titleFadeIn}")
                add("fadeOut = ${it.titleFadeOut}")
            }
        }
    }

    @SubscribeEvent
    fun onConfigFix(event: ConfigFixEvent) {
        event.checkVersion(160) {
            val tomlData = event.tomlData ?: return@checkVersion
            val titleValue = tomlData.get<String>("developer.title./ratesttitle_title")
            val subTitleValue = tomlData.get<String>("developer.title./ratesttitle_subtitle")

            tomlData.add("developer.title./ra_testtitle_title", titleValue)
            tomlData.add("developer.title./ra_testtitle_subtitle", subTitleValue)

            tomlData.remove<String>("developer.title./ratesttitle_title")
            tomlData.remove<String>("developer.title./ratesttitle_subtitle")

            event.tomlData = tomlData
        }
    }

    @SubscribeEvent
    fun onPacket(event: PacketReceivedEvent) {
        if (event.packet !is S45PacketTitle) return

        val chatComponent = event.packet.message ?: return
        val formattedText = chatComponent.formattedText?.cleanupColors() ?: return

        if (formattedText.isEmpty()) return

        val newEvent = TitleReceivedEvent(formattedText, chatComponent, event.packet.type)

        newEvent.post()
        if (newEvent.isCanceled) event.cancel()
    }

    @SubscribeEvent
    fun onTitle(event: TitleReceivedEvent) {
        if (titlesToRender.isEmpty()) return

        event.cancel()
    }

    fun clearVanillaTitle() = Minecraft.getMinecraft().ingameGUI.displayTitle("", "", 0, 0, 0)
}

data class TitleObject(
    val titleText: String = "",
    val subTitleText: String = "",

    val titleDuration: Duration,
    val titleFadeIn: Duration,
    val titleFadeOut: Duration,
) {
    private val creationTime = SimpleTimeMark.now()
    fun isExpired() = (creationTime + titleDuration).isInPast()

    fun getAlpha(partialTicks: Float): Int {
        val fadeOutStart = titleDuration - titleFadeOut
        val interpolatedTime = creationTime.passedSince().inTicks() + partialTicks

        return when (creationTime.passedSince()) {
            in 0.seconds..titleFadeIn -> {
                val alphaMultiplier = interpolatedTime / titleFadeIn.inTicks()
                (alphaMultiplier * 255).toInt()
            }

            in fadeOutStart..titleDuration -> {
                val fadeDuration = (titleDuration - fadeOutStart).inTicks()
                val timeSinceFadeStart = interpolatedTime - fadeOutStart.inTicks()
                val alphaMultiplier = 1.0 - (timeSinceFadeStart / fadeDuration)
                (alphaMultiplier * 255).toInt()
            }

            else -> 255
        }
    }

    private fun Duration.inTicks(): Int = (this.inSeconds() * 20).toInt()
    private fun Duration.inSeconds(): Double = this.inWholeMilliseconds.toDouble() / 1000.0
}