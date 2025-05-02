package at.raven.ravenAddons.utils

import at.raven.ravenAddons.data.commands.CommandCategory
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

@LoadModule
object ClickableChatManager {
    private val commandMap = mutableMapOf<UUID, Clickable>()

    fun createRunnableAction(runnable: (() -> Any), oneTime: Boolean = false, expiresAt: SimpleTimeMark = SimpleTimeMark.farFuture()): UUID {
        val newUUID = UUID.randomUUID()

        commandMap[newUUID] = Clickable(runnable, oneTime, expiresAt)
        return newUUID
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("chat-action") {
            description = "Command used by the mod to execute clickable chat action."
            category = CommandCategory.INTERNAL
            hidden = true
            callback { tryRunCommand(it) }
        }
    }

    private fun tryRunCommand(args: Array<String>) {
        val id = UUID.fromString(args.firstOrNull()) ?: run {
            ChatUtils.warning("Wrong usage! /ra chat-action <id>")
            return
        }
        val clickable = commandMap.getOrElse(id) {
            ChatUtils.warning("ID not found!")
            return
        }

        try {
            if (clickable.expiresAt.isInPast()) {
                commandMap.remove(id)
                return
            }
            clickable.runnable()
            if (clickable.oneTime) {
                commandMap.remove(id)
            }
        } catch (e: Throwable) {
            ChatUtils.chatClickable(
                message = "Caught a ${e.javaClass.simpleName} while executing a runnable! Click here to copy the stacktrace to the clipboard.",
                hoverText = "§7Click here to copy the stacktrace!",
                runnable = {
                    ClipboardUtils.copyToClipboard("ravenAddons ${ravenAddons.MOD_VERSION}\n" + e.stackTraceToString())
                    ChatUtils.chat("Copied to clipboard!")
                }
            )
        }
    }

    class Clickable(
        val runnable: () -> Any,
        val oneTime: Boolean,
        val expiresAt: SimpleTimeMark,
    )
}
