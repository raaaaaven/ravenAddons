package at.raven.ravenaddons.config

import at.raven.ravenaddons.RavenAddons
import at.raven.ravenaddons.event.CommandRegistrationEvent
import at.raven.ravenaddons.event.GameLoadEvent
import at.raven.ravenaddons.loadmodule.LoadModule
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
        configGui = RavenAddonsConfig.gui()
    }

    private fun openConfig() {
        val gui = configGui ?: RavenAddonsConfig.gui() ?: return
        RavenAddons.openScreen(gui)
    }
}