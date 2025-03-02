package at.raven.ravenAddons.config

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.ConfigFixEvent
import at.raven.ravenAddons.event.GameLoadEvent
import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matches
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ConfigCommand {
    private var configGui: GuiScreen? = null
    private var wasModUpdated: Boolean? = null

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

        val configVersion = ravenAddonsConfig.configVersion.toIntOrNull() ?: 0
        if (configVersion < ravenAddons.modVersion) {
            ravenAddonsConfig.configVersion = ravenAddons.modVersion.toString()
            wasModUpdated = true
        }
    }

    @SubscribeEvent
    fun onHypixelJoin(event: HypixelJoinEvent) {
        ChatUtils.chat("ravenAddons successfully updated to version ${ravenAddons.MOD_VERSION}!")
    }

    private fun openConfig() {
        val gui = configGui ?: ravenAddonsConfig.gui() ?: return
        ravenAddons.openScreen(gui)
    }

    private val badConfigLine = "\\t\\t\\[dungeons\\.floor_7\\.better_device_notifications_(?:sub)?title]".toPattern()

    @SubscribeEvent
    fun onConfigFix(event: ConfigFixEvent) {
        if (event.configVersion <= 130) {
            var deleteNext = false
            val newConfig = mutableListOf<String>()

            for (line in event.configLines) {
                if (deleteNext) {
                    deleteNext = false
                    continue
                }

                if (badConfigLine.matches(line)) {
                    deleteNext = true
                } else {
                    newConfig.add(line)
                }
            }

            event.configLines = newConfig
        }
    }
}