package at.raven.ravenAddons.features

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import javax.script.ScriptEngineManager
import kotlin.time.Duration.Companion.seconds

@LoadModule
object QuickMathsSolver {
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (!ravenAddonsConfig.quickMathsSolver) return

        val message = event.message.unformattedText
        val regex = Regex("QUICK MATHS! Solve: (.+)")
        val match = regex.find(message) ?: return
        val expression = match.groupValues[1].replace("x", "*")

        try {
            val engine = ScriptEngineManager().getEngineByName("JavaScript")
            val result = engine.eval(expression)

            ChatUtils.chat("§7The §d§lQUICK MATHS! §r§7answer is §f§l$result§r§7!")
            TitleManager.setTitle("§f§l$result", "§d§lQUICK MATHS!", 2.5.seconds, 0.5.seconds, 0.5.seconds)
        } catch (e: Exception) {
            ChatUtils.warning("Failed to solve QUICK MATHS! (${e.message})")
        }
    }
}