package at.raven.ravenAddons.config.guieditor

import at.raven.ravenAddons.config.guieditor.GuiPositionEditorManager.getDummySize
import at.raven.ravenAddons.config.guieditor.GuiPositionEditorUtils.scaledWindowHeight
import at.raven.ravenAddons.config.guieditor.GuiPositionEditorUtils.scaledWindowWidth
import at.raven.ravenAddons.config.guieditor.data.GuiPosition
import at.raven.ravenAddons.event.render.RenderOverlayEvent
import at.raven.ravenAddons.utils.KeyboardUtils.isKeyHeld
import at.raven.ravenAddons.utils.NumberUtils.roundTo
import at.raven.ravenAddons.utils.render.GuiRenderUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

class GuiPositionEditor(
    private val positions: List<GuiPosition>,
    private val border: Int,
) : GuiScreen() {
    private var grabbedX = 0
    private var grabbedY = 0
    private var clickedIndex: Int? = null
    private var hoveredIndex: Int? = null

    private val isHoldingCtrl get() = Keyboard.KEY_LSHIFT.isKeyHeld()

    override fun onGuiClosed() {
        super.onGuiClosed()
        clickedIndex = null
        hoveredIndex = null
        positions.forEach {
            it.clicked = false
        }
    }

    override fun drawScreen(
        mouseX: Int,
        mouseY: Int,
        partialTicks: Float,
    ) {
        drawDefaultBackground()

        super.drawScreen(mouseX, mouseY, partialTicks)

        renderRectangles()

        renderText()

        GlStateManager.disableLighting()

        RenderOverlayEvent(partialTicks).post()
    }

    override fun mouseClicked(
        originalX: Int,
        originalY: Int,
        mouseButton: Int,
    ) {
        super.mouseClicked(originalX, originalY, mouseButton)

        val (mouseX, mouseY) = GuiPositionEditorUtils.mousePos

        for (i in positions.indices.reversed()) {
            val pos = positions[i]
            val (elementWidth, elementHeight) = pos.getDummySize()
            val x = pos.x
            val y = pos.y
            val isHovered =
                GuiRenderUtils.isPointInRect(
                    mouseX,
                    mouseY,
                    x - border,
                    y - border,
                    x + elementWidth + border * 2,
                    y + elementHeight + border * 2,
                )
            if (!isHovered) continue
//            if (mouseButton == 1 && pos.canJumpToConfigOptions()) {
//                pos.jumpToConfigOption()
//                break
//            }
            if (!pos.clicked && mouseButton == 0) {
                clickedIndex = i
                pos.clicked = true
                grabbedX = mouseX
                grabbedY = mouseY
                break
            }
        }
    }

    override fun mouseReleased(
        mouseX: Int,
        mouseY: Int,
        state: Int,
    ) {
        super.mouseReleased(mouseX, mouseY, state)

        positions.forEach {
            it.clicked = false
        }
    }

    override fun mouseClickMove(
        mouseX: Int,
        mouseY: Int,
        clickedMouseButton: Int,
        timeSinceLastClick: Long,
    ) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)

        for (pos in positions) {
            if (!pos.clicked) continue

            val (newMouseX, newMouseY) = GuiPositionEditorUtils.mousePos
            val (elementWidth, elementHeight) = pos.getDummySize()
            grabbedX += pos.moveX(newMouseX - grabbedX, elementWidth)
            grabbedY += pos.moveY(newMouseY - grabbedY, elementHeight)
        }
    }

    override fun handleMouseInput() {
        super.handleMouseInput()

        val mouseWheel = Mouse.getEventDWheel()
        if (mouseWheel == 0) return

        val active = positions.getOrNull(clickedIndex ?: -1) ?: positions.getOrNull(hoveredIndex ?: -1) ?: return

        var newScale = .1

        if (isHoldingCtrl) newScale *= 10
        if (mouseWheel < 0) newScale *= -1

        val currentScale = active.scale
        val adjustedScale = (currentScale + newScale).coerceIn(0.5, 20.0)

        active.set(scale = adjustedScale)
    }

    override fun keyTyped(
        typedChar: Char,
        keyCode: Int,
    ) {
        super.keyTyped(typedChar, keyCode)

        val clicked = clickedIndex ?: return
        val pos = positions[clicked]
        if (pos.clicked) return

        val multiplier = if (isHoldingCtrl) 10 else 1

        var (elementWidth, elementHeight) = pos.getDummySize()

        when (keyCode) {
            Keyboard.KEY_DOWN -> pos.moveY(multiplier, elementHeight)
            Keyboard.KEY_UP -> pos.moveY(-multiplier, elementHeight)
            Keyboard.KEY_LEFT -> pos.moveX(-multiplier, elementWidth)
            Keyboard.KEY_RIGHT -> pos.moveX(multiplier, elementWidth)
            Keyboard.KEY_MINUS -> pos.scaleKeyBind(0.1 * multiplier - 1)
            Keyboard.KEY_SUBTRACT -> pos.scaleKeyBind(0.1 * multiplier - 1)
            Keyboard.KEY_EQUALS -> pos.scaleKeyBind(0.1 * multiplier)
            Keyboard.KEY_ADD -> pos.scaleKeyBind(0.1 * multiplier)
        }
    }

    private fun GuiPosition.scaleKeyBind(multiplier: Double) {
        this.set(scale = (multiplier + this.scale).coerceIn(0.5, 20.0))
    }

    /**
     * Renders the backgrounds for the GUI elements
     */
    private fun renderRectangles() {
        var hovered: Int? = null
        GlStateManager.pushMatrix()
        width = scaledWindowWidth
        height = scaledWindowHeight

        val (mouseX, mouseY) = GuiPositionEditorUtils.mousePos

        for ((index, pos) in positions.withIndex()) {
            val (elementWidth, elementHeight) = pos.getDummySize()

            if (pos.clicked) {
                grabbedX += pos.moveX(mouseX - grabbedX, elementWidth)
                grabbedY += pos.moveY(mouseY - grabbedY, elementHeight)
            }

            val x = pos.x
            val y = pos.y

            val rectMinX = x - border
            val rectMinY = y - border
            val rectMaxX = x + elementWidth + border * 2
            val rectMaxY = y + elementHeight + border * 2

            drawRect(rectMinX, rectMinY, rectMaxX, rectMaxY, -0x7fbfbfc0)

            val hoveringElement = GuiRenderUtils.isPointInRect(mouseX, mouseY, rectMinX, rectMinY, rectMaxX, rectMaxY)

            if (hoveringElement) {
                hovered = index
            }
        }
        GlStateManager.popMatrix()
        hoveredIndex = hovered
    }

    /**
     * Renders the title and labels
     */
    private fun renderText() {
        GuiRenderUtils.drawStringCentered("§bravenAddons GUI Editor", scaledWindowWidth / 2, 8)

        var displayPos: Int? = null
        val clickedPos = clickedIndex
        if (clickedPos != null && positions[clickedPos].clicked) {
            displayPos = clickedPos
        }
        if (displayPos == null) {
            displayPos = hoveredIndex
        }

        if (displayPos == null) {
            GuiRenderUtils.drawStringCentered(
                "§eClick and drag an element to move it",
                scaledWindowWidth / 2,
                20,
            )
            GuiRenderUtils.drawStringCentered(
                "§ethen use the scroll wheel to resize",
                scaledWindowWidth / 2,
                32,
            )
            return
        }

        val pos = positions[displayPos]
        val location =
            "§7x: §e${pos.x}§7, y: §e${pos.y}§7, scale: §e${pos.scale.roundTo(2)}"

        GuiRenderUtils.drawStringCentered("§b" + pos.label, scaledWindowWidth / 2, 18)
        GuiRenderUtils.drawStringCentered(location, scaledWindowWidth / 2, 28)
//        if (pos.canJumpToConfigOptions()) {
//            GuiRenderUtils.drawStringCentered(
//                "§aRight-Click to open associated config option",
//                scaledWindowWidth / 2,
//                38,
//            )
//        }
    }
}
