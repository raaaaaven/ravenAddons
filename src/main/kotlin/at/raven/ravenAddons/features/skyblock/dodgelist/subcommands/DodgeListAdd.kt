package at.raven.ravenAddons.features.skyblock.dodgelist.subcommands

import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeList
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListCustomData
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListSubcommand
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import at.raven.ravenAddons.utils.PlayerUtils.PlayerIdentifier
import at.raven.ravenAddons.utils.PlayerUtils.getPlayer
import net.minecraft.util.ChatComponentText

object DodgeListAdd: DodgeListSubcommand() {
    override val name = "add"
    override val usage = " <player>"
    override val description = "Add a player to the dodge list with a reason"

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

        actualPlayer.addToList(args.drop(1).joinToString(" ").takeIf { it.isNotEmpty() })
    }

    private fun PlayerIdentifier.addToList(reason: String?) {
        val uuid = this.uuid
        val name = this.name
        val data = DodgeListCustomData(name, reason)

        sendChatMessage(name, data.actualReason)
        DodgeList.addPlayer(uuid, data)
    }

    private fun sendChatMessage(player: String, reason: String) {
        val alreadyOnList = DodgeList.throwers.any { it.value.playerName.lowercase() == player.lowercase() }
        val component = ChatComponentText("")

        component.add(DodgeListChatComponents.getLineComponent())
        if (!alreadyOnList) {
            component.add("§7Added §a$player §7to the list. \n")
            component.add("§f$reason\n")
        } else {
            component.add("§7Player §c$player §7is already in the list.\n")
        }
        component.add(DodgeListChatComponents.getLineComponent(false))

        ChatUtils.chat(component)
    }
}