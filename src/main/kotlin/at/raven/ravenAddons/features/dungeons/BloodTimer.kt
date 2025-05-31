package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ServerTimeMark
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.TimeUtils.inPartialSeconds
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@LoadModule
object BloodTimer {
    private val bloodOpenMessages = setOf(
        "[BOSS] The Watcher: Things feel a little more roomy now, eh?",
        "[BOSS] The Watcher: Oh.. hello?",
        "[BOSS] The Watcher: I'm starting to get tired of seeing you around here...",
        "[BOSS] The Watcher: You've managed to scratch and claw your way here, eh?",
        "[BOSS] The Watcher: So you made it this far... interesting.",
        "[BOSS] The Watcher: Ah, we meet again...",
        "[BOSS] The Watcher: Ah, you've finally arrived.",
    )
    private var bloodOpenTime = SimpleTimeMark.farPast()
    private var bloodOpenLength = ServerTimeMark.FAR_PAST

    @SubscribeEvent
    fun onChatReceived(event: ChatReceivedEvent) {
        if (!isEnabled()) return
        when (event.cleanMessage) {
            in bloodOpenMessages -> {
                bloodOpenTime = SimpleTimeMark.now()
                bloodOpenLength = ServerTimeMark.now()
            }

            "[BOSS] The Watcher: Let's see how you can handle this." -> {
                val bloodMove = bloodOpenTime.passedSince() + 0.1.seconds
                val bloodMoveTime = bloodOpenLength.passedSince() + 0.1.seconds

                val bloodLag = bloodMove - bloodMoveTime

                ChatUtils.debug("bloodMoveTime: $bloodMoveTime")

                // selects move prediction for 4th mob based on how long watcher took to say activation line
                val bloodMovePredictionNumber: kotlin.time.Duration? = when (bloodMoveTime.inPartialSeconds) {
                    in 31.0..34.0 -> bloodLag + 36.seconds
                    in 28.0..31.0 -> bloodLag + 33.seconds
                    in 25.0..28.0 -> bloodLag + 30.seconds
                    in 22.0..25.0 -> bloodLag + 27.seconds
                    in 1.0..21.0 -> bloodLag + 24.seconds
                    else -> null
                }
                val bloodMovePrediction = bloodMovePredictionNumber?.inPartialSeconds?.let { "%.2f".format(it) }

                bloodMovePrediction?.let {
                    ChatUtils.chat("§7Move Prediction: §f$it Seconds§7.")
                    TitleManager.setTitle("", "§7Move Prediction: §f${it}s", 2.5.seconds, 0.seconds, 0.seconds)
                    val delay = bloodMovePredictionNumber - bloodMoveTime - 150.milliseconds
                    ChatUtils.debug("Blood Timer: $delay delay.")
                    ravenAddons.runDelayed(delay) {
                        TitleManager.setTitle("", "§cKill Blood", 1.5.seconds, 0.seconds, 0.seconds)
                    }
                } ?: run {
                    ChatUtils.warning("§cInvalid Prediction")
                }
            }
        }
    }

    fun isEnabled() = (SkyBlockIsland.CATACOMBS.isInIsland() || ravenAddonsConfig.bloodTimer)
}