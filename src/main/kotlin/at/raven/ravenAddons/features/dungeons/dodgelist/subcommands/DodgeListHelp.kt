package at.raven.ravenAddons.features.dungeons.dodgelist.subcommands

import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeList
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListSubcommand
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText

object DodgeListHelp : DodgeListSubcommand() {
    override val name = "help"
    override val description = "Displays this menu"

    override val hasArguments = false

    override suspend fun execute(args: List<String>) {
        val chatComponent = ChatComponentText("")
        chatComponent.add(DodgeListChatComponents.getLineComponent())

        for (command in DodgeList.subcommands) {
            val component = ChatComponentText("§7• §b/dodge ${command.name}${command.usage} §8- §7${command.description}.\n")

            if (!command.hasArguments) {
                component.chatStyle.chatHoverEvent =
                    HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§bClick here to run §e/dodge ${command.name}§b."))
                component.chatStyle.chatClickEvent =
                    ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dodge ${command.name}")
            } else {
                component.chatStyle.chatHoverEvent =
                    HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§cThis command cannot be run from chat."))
            }

            chatComponent.add(component)
        }
        chatComponent.add(DodgeListChatComponents.getLineComponent(false))

        ChatUtils.chat(chatComponent)
    }
}