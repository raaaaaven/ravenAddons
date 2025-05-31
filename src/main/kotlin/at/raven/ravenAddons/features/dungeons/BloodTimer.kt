package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.RealServerTickEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.TitleManager
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.math.floor
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

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
    private var bloodOpenLength = 0

    @SubscribeEvent
    fun onChatReceived(event: ChatReceivedEvent) {
        if (!isEnabled()) return
        when (event.cleanMessage) {
            in bloodOpenMessages -> {
                bloodOpenTime = SimpleTimeMark.now()
                bloodOpenLength = 0
            }

            "[BOSS] The Watcher: Let's see how you can handle this." -> {
                val bloodMove = floor((bloodOpenTime.passedSince().inWholeMilliseconds.toFloat() / 10) / 100) + 0.10f
                val bloodMoveTicks = bloodOpenLength * 0.05f + 0.1f // convert ticks to time to compare for lag calc

                // calcs delay caused by lag
                val bloodLag = bloodMove - bloodMoveTicks

                // selects move prediction for 4th mob based on how long watcher took to say activation line
                val bloodMovePredictionNumber: Float? = when (bloodMoveTicks) {
                    in 31.0..34.0 -> bloodLag + 36
                    in 28.0..31.0 -> bloodLag + 33
                    in 25.0..28.0 -> bloodLag + 30
                    in 22.0..25.0 -> bloodLag + 27
                    in 1.0..21.0 -> bloodLag + 24
                    else -> null
                }
                val bloodMovePrediction = bloodMovePredictionNumber?.let { "%.2f".format(it) }

                bloodMovePrediction?.let {
                    ChatUtils.chat("§3Move Prediction: §6$it §eSeconds")
                    TitleManager.setTitle("§6${it}s", "§3Move Prediction", 2.5.seconds, 0.seconds, 0.seconds)

                    ravenAddons.runDelayed(((bloodMovePredictionNumber - bloodMoveTicks) * 1000.0 - 150).milliseconds) {
                        TitleManager.setTitle("§cKill Blood", "", 1.5.seconds, 0.seconds, 0.seconds)
                    }
                } ?: run {
                    ChatUtils.warning("§cInvalid Prediction")
                }
            }
        }
    }

    @SubscribeEvent
    fun onServerTick(event: RealServerTickEvent) {
        if (!isEnabled()) return
        bloodOpenLength++
    }

    fun isEnabled() = (SkyBlockIsland.CATACOMBS.isInIsland() && ravenAddonsConfig.bloodTimer)
}