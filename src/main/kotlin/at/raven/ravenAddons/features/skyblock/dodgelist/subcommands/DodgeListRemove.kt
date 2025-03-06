package at.raven.ravenAddons.features.skyblock.dodgelist.subcommands

import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeList
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListSubcommand
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import net.minecraft.util.ChatComponentText

object DodgeListRemove : DodgeListSubcommand() {
    override val name = "remove"
    override val usage = " <player>"
    override val description = "Remove a player from the dodge list"

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

        val oldMapSize = DodgeList.throwers.size
        val iterator = DodgeList.throwers.entries.iterator()
        while (iterator.hasNext()) {
            val member = iterator.next()

            if (member.value.playerName.lowercase() == playerName.lowercase()) {
                DodgeList.removePlayer(member.key)
            }
        }

        sendChatMessage(playerName, oldMapSize != DodgeList.throwers.size)
    }

    private fun sendChatMessage(player: String, couldRemove: Boolean) {
        val component = ChatComponentText("")

        component.add(DodgeListChatComponents.getLineComponent())
        if (couldRemove) {
            component.add("§7Removed §c$player §7from the list. \n")
        } else {
            component.add("§7Player §c$player §7not found in the list.\n")
        }
        component.add(DodgeListChatComponents.getLineComponent(false))

        ChatUtils.chat(component)
    }
}