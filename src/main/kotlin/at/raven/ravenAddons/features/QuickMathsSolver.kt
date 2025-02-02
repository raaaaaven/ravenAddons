package at.raven.ravenAddons.features

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlayingAny
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.Calculator.calc
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object QuickMathsSolver {
    private val pattern = "QUICK MATHS! Solve: (?<expression>.+)".toPattern()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (listOf(HypixelGame.SKYBLOCK, HypixelGame.THE_PIT).isNotPlayingAny()) return
        if (!ravenAddonsConfig.quickMathsSolver) return
        val message = event.message.unformattedText

        pattern.matchMatcher(message) {
            try {
                val expression = group("expression") ?: return
                val resultNumber = expression.calc() ?: run {
                    ChatUtils.warning("Invalid input: '$expression'")
                    return
                }
                val result = resultNumber.toString().removeSuffix(".0")

                ChatUtils.chat("§7The §d§lQUICK MATHS! §r§7answer is §f§l$result§r§7!")
                TitleManager.setTitle("§f§l$result", "§d§lQUICK MATHS!", 2.5.seconds, 0.5.seconds, 0.5.seconds)
            } catch (e: Exception) {
                e.printStackTrace()
                ChatUtils.warning("Failed to solve QUICK MATHS! (${e.message})")
            }
        }
    }
}
