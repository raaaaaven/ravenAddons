package at.raven.ravenAddons.config

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.GameLoadEvent
import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ConfigCommand {
    private var configGui: GuiScreen? = null
    private var wasModUpdated = ModUpdateStatus.NONE

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("ravenaddons") {
            description = "Opens the Config GUI"
            aliases = listOf("raven", "ra")
            callback { openConfig() }
        }
    }

    @SubscribeEvent
    fun onGameLoad(event: GameLoadEvent) {
        ConfigFixer
        configGui = ravenAddonsConfig.gui()

        if (ravenAddonsConfig.configVersion < ravenAddons.modVersion) {
            ravenAddonsConfig.configVersion = ravenAddons.modVersion
            ravenAddonsConfig.markDirty()
            wasModUpdated = ModUpdateStatus.UPDATED
        } else if (ravenAddonsConfig.configVersion > ravenAddons.modVersion) {
            ravenAddonsConfig.configVersion = ravenAddons.modVersion
            ravenAddonsConfig.markDirty()
            wasModUpdated = ModUpdateStatus.DOWNGRADED
        }
    }

    @SubscribeEvent
    fun onHypixelJoin(event: HypixelJoinEvent) {
        val message = wasModUpdated.updateMessage ?: return

        if (wasModUpdated.warning) {
            ChatUtils.warning(message)
        } else {
            ChatUtils.chat(message)
        }
    }

    private fun openConfig() {
        val gui = configGui ?: ravenAddonsConfig.gui() ?: return
        ravenAddons.openScreen(gui)
    }
}
enum class ModUpdateStatus(val updateMessage: String? = null, val warning: Boolean = false) {
    NONE,
    UPDATED("ravenAddons successfully updated to version ${ravenAddons.MOD_VERSION}!"),
    DOWNGRADED("ravenAddons was downgraded to ${ravenAddons.MOD_VERSION}! May cause issues!"),
    ;
}