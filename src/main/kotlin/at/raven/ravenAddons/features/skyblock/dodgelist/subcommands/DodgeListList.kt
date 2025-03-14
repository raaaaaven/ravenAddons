package at.raven.ravenAddons.features.skyblock.dodgelist.subcommands

import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeList.throwers
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListSubcommand
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import net.minecraft.util.ChatComponentText

object DodgeListList: DodgeListSubcommand() {
    override val name = "list"
    override val description = "List everyone on the dodge list"

    override val hasArguments = false

    override suspend fun execute(args: List<String>) {
        val map = throwers.toMap()
        val component = ChatComponentText("")
        component.add(DodgeListChatComponents.getLineComponent())

        if (map.isEmpty()) {
            component.add(" §7The dodge list is currently empty!\n")
        } else {
            for ((_, data) in map) {
                var string = "§7• §b${data.playerName}§7: §f${data.actualReason}"

                data.formattedTimeRemaining?.let {
                    string += " §a($it)"
                }

                component.add(string + "\n")
            }
        }

        component.add(DodgeListChatComponents.getLineComponent(false))
        ChatUtils.chat(component)
    }
}