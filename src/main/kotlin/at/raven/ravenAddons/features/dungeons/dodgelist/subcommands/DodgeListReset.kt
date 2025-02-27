package at.raven.ravenAddons.features.dungeons.dodgelist.subcommands

import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeList
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeList.throwers
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListSubcommand
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import java.lang.Thread.sleep

object DodgeListReset: DodgeListSubcommand() {
    override val name = "reset"
    override val description = "Remove all players from the dodge list"

    override val hasArguments = false
    override val aliases = listOf("clear")

    private var resetConfirmation = false

    override suspend fun execute(args: List<String>) {
        if (!resetConfirmation) {
            resetConfirmation = true

            ChatUtils.chat(resetConfirmationComponent)
            ravenAddons.launchCoroutine {
                sleep(5000)
                if (resetConfirmation == false) return@launchCoroutine
                resetConfirmation = false
                ChatUtils.chat("§cThe confirmation period expired.")
            }
            return
        }

        resetConfirmation = false
        throwers.clear()
        DodgeList.saveToFile()
        ChatUtils.chat("§bSuccessfully reset the list.")
    }

    private val resetConfirmationComponent: ChatComponentText get() {
        val component = ChatComponentText("")
        component.add(DodgeListChatComponents.prefixComponent)
        component.add("§cType §e/dodge reset §cagain to confirm.")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dodge reset")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to confirm resetting the list."))

        return component
    }
}