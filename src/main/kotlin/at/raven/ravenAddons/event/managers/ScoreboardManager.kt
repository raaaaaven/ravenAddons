package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.PacketReceivedEvent
import at.raven.ravenAddons.event.ScoreboardUpdateEvent
import at.raven.ravenAddons.event.TickEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ClipboardUtils
import at.raven.ravenAddons.utils.StringUtils.lastColorCode
import net.minecraft.client.Minecraft
import net.minecraft.network.play.server.S3BPacketScoreboardObjective
import net.minecraft.network.play.server.S3CPacketUpdateScore
import net.minecraft.network.play.server.S3EPacketTeams
import net.minecraft.scoreboard.IScoreObjectiveCriteria
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ScoreboardManager {
    private var dirty = false
    private val world get() = Minecraft.getMinecraft().theWorld

    var scoreboardLinesRaw = listOf<String>()
        private set
    var scoreboardLines = listOf<String>()
        private set
    val objectiveTitle: String
        get() =
            world
                ?.scoreboard
                ?.getObjectiveInDisplaySlot(1)
                ?.displayName
                .orEmpty()

    @SubscribeEvent
    fun onPacketReceive(event: PacketReceivedEvent) {
        val packet = event.packet

        when (packet) {
            is S3CPacketUpdateScore -> {
                if (packet.objectiveName == "update") {
                    dirty = true
                }
            }

            is S3EPacketTeams -> {
                if (packet.name.startsWith("team_")) {
                    dirty = true
                }
            }

            is S3BPacketScoreboardObjective -> {
                val type = packet.func_179817_d()
                if (type != IScoreObjectiveCriteria.EnumRenderType.INTEGER) return
                val objectiveName = packet.func_149339_c()
                if (objectiveName == "health") return
                val objectiveValue = packet.func_149337_d()
                ScoreboardUpdateEvent.Title(objectiveValue, objectiveName).post()
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent) {
        if (!dirty) return
        dirty = false

        val list = fetchScoreboardLines().reversed()

        scoreboardLinesRaw = list
        val new = formatLines(list)
        if (new != scoreboardLines) {
            val old = scoreboardLines
            scoreboardLines = new
            ScoreboardUpdateEvent.Content(old, new).post()
        }
    }

    private fun fetchScoreboardLines(): List<String> {
        val scoreboard = world?.scoreboard ?: return emptyList()
        val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return emptyList()
        var scores = scoreboard.getSortedScores(objective)
        val list =
            scores.filter {
                it != null && it.playerName != null && !it.playerName.startsWith("#")
            }
        scores =
            if (list.size > 15) {
                list.drop(15)
            } else {
                list
            }

        return scores.map { ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(it.playerName), it.playerName) }
    }

    private fun formatLines(rawList: List<String>) =
        buildList {
            for (line in rawList) {
                val separator = splitIcons.find { line.contains(it) } ?: continue
                val split = line.split(separator)
                val start = split[0]
                var end = if (split.size > 1) split[1] else ""

                val lastColor = start.lastColorCode().orEmpty()

                val colorSuffixes = lastColor.chunked(2).toMutableList()

                for (suffix in colorSuffixes.toList()) {
                    if (end.startsWith(suffix)) {
                        end = end.removePrefix(suffix)
                        colorSuffixes.remove(suffix)
                    }
                }

                add(start + end)
            }
        }

    val splitIcons =
        listOf(
            "\uD83C\uDF6B",
            "\uD83D\uDCA3",
            "\uD83D\uDC7D",
            "\uD83D\uDD2E",
            "\uD83D\uDC0D",
            "\uD83D\uDC7E",
            "\uD83C\uDF20",
            "\uD83C\uDF6D",
            "⚽",
            "\uD83C\uDFC0",
            "\uD83D\uDC79",
            "\uD83C\uDF81",
            "\uD83C\uDF89",
            "\uD83C\uDF82",
            "\uD83D\uDD2B",
        )

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("copyscoreboard") {
            description = "Copies the current scoreboard to the clipboard."
            callback { copyScoreboard() }
        }
    }

    private fun copyScoreboard() {
        val title = objectiveTitle + "\n\n"

        val body = scoreboardLines.joinToString("\n")

        ClipboardUtils.copyToClipboard(title + body)
        ChatUtils.debug("Copied scoreboard data to the clipboard")
    }
}
