package at.raven.ravenAddons.config

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.GameLoadEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ConfigCommand {
    private var configGui: GuiScreen? = null

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
        configGui = ravenAddonsConfig.gui()
    }

    private fun openConfig() {
        val gui = configGui ?: return
        ravenAddons.openScreen(gui)
    }
}