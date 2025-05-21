package at.raven.ravenAddons.config

import at.raven.ravenAddons.data.commands.CommandBuilder
import at.raven.ravenAddons.data.commands.CommandCategory
import at.raven.ravenAddons.data.commands.CommandManager.commandList
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import me.owdding.ktmodules.Module
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module
object CommandList {
    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("commands") {
            description = "Prints all commands and their descriptions in chat"
            aliases = listOf("help")
            callback { commandListCommand() }
        }
    }

    private fun commandListCommand() {
        var message = ChatComponentText("")

        var extraLines = mutableListOf(ChatComponentText("§8§m-----------------------------------------------------"))

        val sortedCommandList = commandList.sortedWith(
            compareBy<CommandBuilder> { it.category.ordinal }.thenBy { it.name }
        )

        for (command in sortedCommandList) {
            if (command.hidden && !ravenAddonsConfig.debugMessages) continue

            val clickableCommand = ChatComponentText("\n §7• §${command.category.colorCode}${command.name}")
            val tooltipComponent = ChatComponentText("§bClick to run /ra ${command.name}\n")
            tooltipComponent.add("§7${command.description}")
            if (command.category != CommandCategory.NORMAL) tooltipComponent.add("\n\n${command.category} Command")

            clickableCommand.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ra ${command.name}")
            clickableCommand.chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipComponent)

            for (alias in command.aliases.sortedBy { it }) {
                val tooltipComponent = ChatComponentText("§bClick to run /ra $alias\n")
                tooltipComponent.add("§7${command.description}")
                if (command.category != CommandCategory.NORMAL) tooltipComponent.add("\n\n${command.category} Command")

                clickableCommand.add(ChatComponentText("§8, "))
                clickableCommand.add(ChatComponentText("§${command.category.colorCode}$alias"))
                clickableCommand.siblings
                    .last()
                    .chatStyle.chatClickEvent =
                    ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ra $alias")
                clickableCommand.siblings
                    .last()
                    .chatStyle.chatHoverEvent =
                    HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        tooltipComponent,
                    )
            }
            extraLines += clickableCommand
        }

        extraLines.add(ChatComponentText("\n§8§m-----------------------------------------------------"))
        message.siblings.addAll(extraLines)

        ChatUtils.chat(message)
    }
}