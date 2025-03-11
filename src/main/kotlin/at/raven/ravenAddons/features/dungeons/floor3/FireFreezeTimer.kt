package at.raven.ravenAddons.features.dungeons.floor3

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.SoundUtils
import at.raven.ravenAddons.utils.StringUtils.removeColors
import at.raven.ravenAddons.utils.TitleManager
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object FireFreezeTimer {

        // REGEX TEST: "§r§c[BOSS] The Professor§r§f: Oh? You found my Guardians' one weakness?§r"
        private val professorPattern = "^\\[BOSS] The Professor: Oh\\? You found my Guardians' one weakness\\?".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!ravenAddonsConfig.floor3FireFreezeTimer) return

        professorPattern.matchMatcher(event.message.removeColors()) {
            ChatUtils.debug("floor3FireFreezeTimer: timer started")

            val time = ravenAddonsConfig.floor3FireFreezeDuration.seconds

            ravenAddons.launchCoroutine {
                var timer = 5.00

                while (timer >= 0) {
                    if (timer <= time.inWholeSeconds) {
                        val color = when {
                            timer > 3.0 -> "§a"
                            timer > 1.0 -> "§6"
                            else -> "§c"
                        }
                        TitleManager.setTitle("$color§l%.2f".format(timer),"$color§lFIRE FREEZE", 1.seconds, 0.seconds, 0.seconds)
                    }
                    timer -= 0.01
                    delay(10)
                }

                TitleManager.setTitle("§c§lNOW!", "§c§lFIRE FREEZE", 2.5.seconds, 0.seconds, 0.seconds)
                SoundUtils.playSound(ravenAddonsConfig.floor3FireFreezeSound, 1f, 1f)
            }
        }
    }
}
