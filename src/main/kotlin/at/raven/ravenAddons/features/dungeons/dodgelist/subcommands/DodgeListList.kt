package at.raven.ravenAddons.features.dungeons.dodgelist.subcommands

import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeList
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeList.throwers
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents.add
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListSubcommand
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.PlayerUtils.getPlayer
import net.minecraft.util.ChatComponentText
import java.util.UUID

object DodgeListList: DodgeListSubcommand() {
    override val name = "list"
    override val description = "List everyone on the dodge list"

    override val hasArguments = false

    override suspend fun execute(args: List<String>) {
        ravenAddons.launchCoroutine {
            ChatUtils.chat(createChatComponent())
        }
    }

    private suspend fun createChatComponent(): ChatComponentText {
        val map = throwers.toMap()
        val component = ChatComponentText("")
        component.add(DodgeListChatComponents.getLineComponent())

        if (map.isEmpty()) {
            component.add(listEmpty)
            component.add(DodgeListChatComponents.getLineComponent())
            return component
        }

        val unknownPlayers = mutableListOf<UUID>()
        val changedNames = mutableMapOf<String, String>() //old name -> new name

        for ((uuid, data) in map) {
            val player = uuid.getPlayer() ?: run { unknownPlayers.add(uuid); continue }

            if (player.name != data.playerName) {
                changedNames.put(data.playerName, player.name)
                DodgeList.addPlayer(uuid, player.name, data.reason)
            }

            component.add("§7• §b${player.name}§7: §f${data.reason}\n")
        }
        DodgeList.saveToFile()

        //TODO: display unknownPlayers and changedNames somehow

        component.add(DodgeListChatComponents.getLineComponent(false))
        return component
    }

    private val listEmpty = ChatComponentText(" §7The dodge list is currently empty!\n")
}