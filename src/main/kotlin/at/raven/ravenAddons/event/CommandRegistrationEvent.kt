package at.raven.ravenAddons.event

import at.raven.ravenAddons.data.commands.CommandBuilder
import at.raven.ravenAddons.data.commands.CommandManager.commandList
import at.raven.ravenAddons.event.base.RavenEvent

class CommandRegistrationEvent : RavenEvent() {
    fun register(
        name: String,
        block: CommandBuilder.() -> Unit,
    ) {
        val info = CommandBuilder(name).apply(block)
        if (commandList.any { it.name == name }) {
            error("The command '$name is already registered!'")
        }
        commandList.add(info)
    }
}
