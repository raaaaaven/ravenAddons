package at.raven.ravenAddons.features.skyblock.dodgelist.subcommands

import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeList
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListCustomData
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListSubcommand
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import at.raven.ravenAddons.utils.PlayerUtils.PlayerIdentifier
import at.raven.ravenAddons.utils.PlayerUtils.getPlayer
import at.raven.ravenAddons.utils.SimpleTimeMark
import net.minecraft.util.ChatComponentText
import kotlin.time.Duration

object DodgeListTempAdd: DodgeListSubcommand() {
    override val name = "tempadd"
    override val usage = " <player> <duration> [reason]"
    override val description = "Add a player to the dodge list with an expiry date and reason"

    override val hasArguments = true

    override suspend fun execute(args: List<String>) {
        if (args.isEmpty()) {
            unknownUsage()
            return
        }

        val playerName = args.firstOrNull() ?: run {
            ChatUtils.debug("Non-empty but no first element!")
            return
        }
        val actualPlayer = playerName.getPlayer() ?: run {
            ChatUtils.warning("Couldn't find player $playerName.")
            return
        }

        val duration = Duration.parseOrNull(args.getOrNull(1).toString()) ?: run {
            unknownUsage()
            return
        }
        val reason = args.drop(2).joinToString(" ").takeIf { it.isNotEmpty() }

        actualPlayer.addToList(duration, reason)
    }

    private fun PlayerIdentifier.addToList(duration: Duration, reason: String?) {
        val uuid = this.uuid
        val name = this.name
        val data = DodgeListCustomData(name, reason, SimpleTimeMark.now() + duration)

        sendChatMessage(name, data.actualReason, duration.toString())
        DodgeList.addPlayer(uuid, data)
    }

    private fun sendChatMessage(player: String, reason: String, duration: String) {
        val alreadyOnList = DodgeList.throwers.any { it.value.playerName.lowercase() == player.lowercase() }
        val component = ChatComponentText("")

        component.add(DodgeListChatComponents.getLineComponent())
        if (!alreadyOnList) {
            component.add("§7Added §a$player §7to the list. \n")
            component.add("§b$duration\n")
            component.add("§f$reason\n")
        } else {
            component.add("§7Player §c$player §7is already in the list.\n")
        }
        component.add(DodgeListChatComponents.getLineComponent(false))

        ChatUtils.chat(component)
    }
}