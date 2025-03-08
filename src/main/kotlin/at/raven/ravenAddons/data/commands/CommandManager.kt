package at.raven.ravenAddons.data.commands

import at.raven.ravenAddons.config.ConfigCommand
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraftforge.client.ClientCommandHandler

@LoadModule
object CommandManager {
    private val mainCommand = SimpleCommand(
        "ravenAddons",
        listOf("ra", "raven", "ravenaddons"),
        { mainCommand(it) },
    )
    init {
        ClientCommandHandler.instance.registerCommand(mainCommand)
    }

    val commandList = mutableListOf<CommandBuilder>()

    private fun mainCommand(args: Array<String>) {
        if (args.isEmpty()) {
            ConfigCommand.openConfig()
            return
        }

        val subcommand = commandList.firstOrNull { it.name == args[0] }

        if (subcommand == null) {
            showHelpMessage()
            return
        }

        subcommand.callback.invoke(args.drop(1).toTypedArray())
    }

    private fun showHelpMessage() {
        val message = ChatComponentText("§7Command not found, click here to see the command list!")
        message.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ravenaddons commands")
        message.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§8Click here to run §7/ravenaddons commands"))

        val finalMessage = ChatComponentText("")
        finalMessage.siblings.addAll(listOf(ChatUtils.prefixChatComponent, message))

        ChatUtils.chat(finalMessage)
    }
}
