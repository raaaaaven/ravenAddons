package at.raven.ravenAddons.config.guieditor

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.input.Mouse
import java.lang.reflect.Field

object GuiPositionEditorUtils {
    private val mc get() = Minecraft.getMinecraft()

    val scaledWindowHeight get() = ScaledResolution(mc).scaledHeight
    val scaledWindowWidth get() = ScaledResolution(mc).scaledWidth

    val displayHeight get() = mc.displayHeight
    val displayWidth get() = mc.displayWidth

    private val globalMouseX get() = Mouse.getX()
    private val globalMouseY get() = Mouse.getY()

    val mouseX get() = globalMouseX * scaledWindowWidth / displayWidth
    val mouseY: Int
        get() {
            val height = this.scaledWindowHeight
            val y = globalMouseY * height / displayHeight
            return height - y - 1
        }

    val mousePos get() = mouseX to mouseY

    fun Field.makeAccessible() = also { isAccessible = true }

    // keyboard utils when

}
