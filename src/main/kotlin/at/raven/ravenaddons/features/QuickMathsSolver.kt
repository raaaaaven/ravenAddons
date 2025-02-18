package at.raven.ravenaddons.features

import at.raven.ravenaddons.config.RavenAddonsConfig
import at.raven.ravenaddons.data.HypixelGame
import at.raven.ravenaddons.data.HypixelGame.Companion.isNotPlayingAny
import at.raven.ravenaddons.loadmodule.LoadModule
import at.raven.ravenaddons.utils.Calculator.calc
import at.raven.ravenaddons.utils.ChatUtils
import at.raven.ravenaddons.utils.RegexUtils.matchMatcher
import at.raven.ravenaddons.utils.TitleManager
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object QuickMathsSolver {
    private val pattern = "QUICK MATHS! Solve: (?<expression>.+)".toPattern()

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (listOf(HypixelGame.SKYBLOCK, HypixelGame.THE_PIT).isNotPlayingAny()) return
        if (!RavenAddonsConfig.quickMathsSolver) return
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
