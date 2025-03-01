package at.raven.ravenAddons.features.skyblock.dodgelist

import at.raven.ravenAddons.utils.ChatUtils

abstract class DodgeListSubcommand {
    abstract val name: String
    open val usage = ""
    abstract val description: String

    abstract val hasArguments: Boolean

    abstract suspend fun execute(args: List<String>)

    open val aliases: List<String> = emptyList()
    open fun unknownUsage() = ChatUtils.warning("Wrong Usage! /dodge $name$usage")
}