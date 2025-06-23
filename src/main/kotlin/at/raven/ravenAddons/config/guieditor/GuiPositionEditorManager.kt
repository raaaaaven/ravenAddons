package at.raven.ravenAddons.config.guieditor

import at.raven.ravenAddons.config.guieditor.data.GuiPosition
import at.raven.ravenAddons.config.guieditor.data.TimeLimitedCache
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.DebugDataCollectionEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.StringUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object GuiPositionEditorManager {
    private val positions = TimeLimitedCache<String, GuiPosition>(15.seconds)
    private val currentBorderSize =
        mutableMapOf<String, Pair<Int, Int>>()

    fun add(
        position: GuiPosition,
        label: String,
        width: Int,
        height: Int,
    ) {
        var name = position.internalName
        if (name == null) {
            name = if (label == "none") "none " + StringUtils.generateRandomId() else label
            position.internalName = name
        }
        position.setLabel(label)
        positions[name] = position
        currentBorderSize[label] = width to height
    }

    fun isInGui() = Minecraft.getMinecraft().currentScreen is GuiPositionEditor

    fun openGui() =
        ravenAddons.openScreen(
            GuiPositionEditor(
                positions.values.toList(),
                2,
            ),
        )

    fun GuiPosition.getDummySize(random: Boolean = false): Pair<Int, Int> {
        if (random) return 5 to 5
        return currentBorderSize[internalName] ?: (1 to 1)
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("gui") {
            description = "Edit GUI Positions"
            callback { openGui() }
        }
    }

    @SubscribeEvent
    fun onDebug(event: DebugDataCollectionEvent) {
        event.title("Gui Position Manager")
        event.addData {
            positions.values.toList().forEach {
                add(it.label + " | " + it.internalName)
            }
        }
    }
}
