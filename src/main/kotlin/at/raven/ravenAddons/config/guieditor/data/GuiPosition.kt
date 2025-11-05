package at.raven.ravenAddons.config.guieditor.data

import at.raven.ravenAddons.ravenAddons.Companion.mc
import com.google.gson.annotations.Expose
import net.minecraft.client.gui.ScaledResolution
import java.lang.reflect.Field

class GuiPosition() {
    @Expose
    var x: Int = 10
        private set

    @Expose
    var y: Int = 10
        private set

    @Expose
    var scale: Double = 1.0
        private set

    var clicked = false
    var label = ""
        private set
    var internalName: String? = null
    var configField: Field? = null

    constructor(x: Int, y: Int, scale: Double) : this() {
        this.x = x
        this.y = y
        this.scale = scale
    }

    constructor(x: Int, y: Int) : this() {
        this.x = x
        this.y = y
    }

    fun setLabel(label: String) {
        this.label = label
    }

    fun add(
        x: Int = 0,
        y: Int = 0,
        scale: Double = 0.0,
    ) {
        this.x += x
        this.y += y
        this.scale += scale
    }

    fun set(
        x: Int? = null,
        y: Int? = null,
        scale: Double? = null,
    ) {
        x?.let { this.x = x }
        y?.let { this.y = y }
        scale?.let { this.scale = scale }
    }

    fun moveX(
        deltaX: Int,
        objWidth: Int,
    ): Int {
        val screenWidth = ScaledResolution(mc).scaledWidth
        val newX = this.x + deltaX

        val adjustedDeltaX =
            when {
                newX < 0 -> -this.x
                newX > screenWidth - objWidth -> screenWidth - objWidth - this.x
                else -> deltaX
            }

        this.x += adjustedDeltaX
        return adjustedDeltaX
    }

    fun moveY(
        deltaY: Int,
        objHeight: Int,
    ): Int {
        val screenHeight = ScaledResolution(mc).scaledHeight
        val newY = this.y + deltaY

        val adjustedDeltaY =
            when {
                newY < 0 -> -this.y
                newY > screenHeight - objHeight -> screenHeight - objHeight - this.y
                else -> deltaY
            }

        this.y += adjustedDeltaY
        return adjustedDeltaY
    }

    // TODO: move away to a better config
//    @Throws(NoSuchFieldException::class)
//    fun setLink(configLink: ConfigLink) {
//        this.configField = configLink.owner.java.getDeclaredField(configLink.field)
//    }

//    @Throws(NoSuchFieldException::class)
//    fun canJumpToConfigOptions(): Boolean =
//        configField != null && ConfigGuiManager.getEditorInstance().getOptionFromField(configField) != null
//
//    fun jumpToConfigOption() {
//        val editor = ConfigGuiManager.getEditorInstance()
//        if (configField == null) return
//        val option = editor.getOptionFromField(configField) ?: return
//        editor.search("")
//        if (!editor.goToOption(option)) return
//        Awesome.openScreen(GuiScreenElementWrapper(editor))
//    }
}
