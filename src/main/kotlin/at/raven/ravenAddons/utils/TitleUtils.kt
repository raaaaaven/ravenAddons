package at.raven.ravenAddons.utils

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@LoadModule
object TitleUtils {
    fun showTitle(title: String, duration: Duration = 3.5.seconds, subtitle: String? = null) {
        Minecraft.getMinecraft().ingameGUI.displayTitle(title, subtitle, 10, (duration.inWholeMilliseconds / 50).toInt(), 20)
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("rashowtitle") {
            description = "Display a test title"
            callback {
                ChatUtils.chat("Attempting to display the test title")
                showTitle("test", subtitle = "test", duration = 3.seconds) }
        }
    }
}