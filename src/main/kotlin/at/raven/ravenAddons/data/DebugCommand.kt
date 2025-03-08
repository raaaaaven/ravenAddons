package at.raven.ravenAddons.data

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.DebugDataCollectionEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ClipboardUtils
import at.raven.ravenAddons.utils.EventUtils.post
import at.raven.ravenAddons.utils.StringUtils.equalsIgnoreColor
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object DebugCommand {
    fun command(args: Array<String>) {
        val list = mutableListOf<String>()

        list.add("```")
        list.add("= Debug Information for ravenAddons ${ravenAddons.MOD_VERSION} =")
        list.add("")

        val search = args.joinToString(" ")
        list.add(
            if (search.isNotEmpty()) {
                if (search.equalsIgnoreColor("all")) {
                    "Search for everything:"
                } else {
                    "Search '$search':"
                }
            } else {
                "No search specified! Only showing interesting stuff:"
            },
        )

        val event = DebugDataCollectionEvent(list, search)

        event.post()
        if (event.empty) {
            list.add("")
            list.add("Nothing interesting to show right now!")
            list.add("Looking for something specific? /radebug <search>")
            list.add("Display everything? /radebug all")
        }

        list.add("```")
        ClipboardUtils.copyToClipboard(list.joinToString("\n"))
        ChatUtils.chat("Copied ravenAddons debug data to the clipboard.")
    }

    private fun version() {
        ChatUtils.chat("ravenAddons Version: ${ravenAddons.MOD_VERSION}")
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("debug") {
            description = "Copies important(?) debug data to the clipboard."
            callback { command(it) }
        }

        event.register("version") {
            description = "State the version of ravenAddons you are currently using."
            callback { version() }
        }
    }
}