package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.event.TickEvent
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

@LoadModule
object BloodTimer {
    private val bloodOpenMessages = listOf(
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
            in (bloodOpenMessages) -> {
                bloodOpenTime = SimpleTimeMark.now()
                bloodOpenLength = 0
            }
            "[BOSS] The Watcher: Let's see how you can handle this." -> {
                val bloodMove = String.format("%.2f", (floor((SimpleTimeMark.now().toMillis().toFloat() - bloodOpenTime.toMillis().toFloat())/10)/100)+0.10).toFloat()
                val bloodMoveTicks = String.format("%.2f", (bloodOpenLength*0.05+0.1)).toFloat()

                val bloodLag = bloodMove - bloodMoveTicks
                val bloodMovePrediction = if (bloodMoveTicks >= 31 && bloodMoveTicks <= 33.99) String.format("%.2f", 36 + bloodLag)
                else if (bloodMoveTicks >= 28 && bloodMoveTicks <= 30.99) String.format("%.2f", 33 + bloodLag)
                else if (bloodMoveTicks >= 25 && bloodMoveTicks <= 27.99) String.format("%.2f", 30 + bloodLag)
                else if (bloodMoveTicks >= 22 && bloodMoveTicks <= 24.99) String.format("%.2f", 27 + bloodLag)
                else if (bloodMoveTicks >= 1 && bloodMoveTicks <= 21.99) String.format("%.2f", 24 + bloodLag)
                else ""

                ChatUtils.chat(if (bloodMovePrediction != "") "§3Move Prediction: §6$bloodMovePrediction §eSeconds" else "§cInvalid Prediction")
                TitleManager.setTitle(if (bloodMovePrediction != "") "§6${bloodMovePrediction}s" else "§cInvalid Prediction", "§3Move Prediction", 2.5.seconds, 0.seconds, 0.seconds)
                ravenAddons.runDelayed(
                    (((bloodMovePrediction.toFloatOrNull()?.minus(bloodMoveTicks))?.times(1000))?.minus(150))?.toInt()?.milliseconds
                        ?: 0.seconds
                ) { TitleManager.setTitle(if (bloodMovePrediction != "") "§cKill Blood" else "§cInvalid Prediction", "", 1.5.seconds, 0.seconds, 0.seconds) }
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent) {
        if (!isEnabled()) return
        bloodOpenLength++
    }

    fun isEnabled() = (SkyBlockIsland.CATACOMBS.isInIsland() && ravenAddonsConfig.bloodTimer)
}