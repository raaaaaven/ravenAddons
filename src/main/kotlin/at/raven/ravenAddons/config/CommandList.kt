package at.raven.ravenAddons.config

import at.raven.ravenAddons.data.commands.CommandManager.commandList
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object CommandList {
    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("commands") {
            description = "Prints all commands and their descriptions in chat"
            callback { commandListCommand() }
        }
    }

    private fun commandListCommand() {
        var message = ChatComponentText("")

        var extraLines = mutableListOf(ChatComponentText("§7---------------------------------------------------"))

        commandList.forEachIndexed { index, command ->

            val clickableCommand = ChatComponentText("\n§2${command.name}")
            clickableCommand.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ravenaddons ${command.name}")
            clickableCommand.chatStyle.chatHoverEvent =
                HoverEvent(
                    HoverEvent.Action.SHOW_TEXT,
                    ChatComponentText("Click to run §2/ravenaddons ${command.name}"),
                )

            command.aliases.forEach { alias ->
                clickableCommand.siblings.add(ChatComponentText("§8, "))
                clickableCommand.siblings.add(ChatComponentText("§a$alias"))
                clickableCommand.siblings
                    .last()
                    .chatStyle.chatClickEvent =
                    ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ravenaddons $alias")
                clickableCommand.siblings
                    .last()
                    .chatStyle.chatHoverEvent =
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        ChatComponentText("Click to run §a/ravenaddons $alias"),
                    )
            }
            extraLines += clickableCommand
            extraLines += ChatComponentText("\n§e" + command.description)

            if (index != (commandList.size - 1)) extraLines.add(ChatComponentText("\n"))
        }
        extraLines.add(ChatComponentText("\n§7---------------------------------------------------"))
        message.siblings.addAll(extraLines)

        ChatUtils.chat(message)
    }
}