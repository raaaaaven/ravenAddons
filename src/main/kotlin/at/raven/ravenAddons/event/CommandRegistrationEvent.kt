package at.raven.ravenAddons.event

import at.raven.ravenAddons.data.commands.CommandBuilder
import at.raven.ravenAddons.data.commands.CommandManager.commandList
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.eventhandler.Event

class CommandRegistrationEvent : Event() {
    fun register(
        name: String,
        block: CommandBuilder.() -> Unit,
    ) {
        val info = CommandBuilder(name).apply(block)
        if (commandList.any { it.name == name }) {
            error("The command '$name is already registered!'")
        }
        ClientCommandHandler.instance.registerCommand(info.toSimpleCommand())
        commandList.add(info)
    }
}
