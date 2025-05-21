package at.raven.ravenAddons.features.skyblock.dodgelist

import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ClickableChatManager
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText

object DodgeListChatComponents {
    fun getLineComponent(withNewLine: Boolean = true) =
        if (withNewLine) ChatComponentText("§8§m-----------------------------------------------------\n")
        else ChatComponentText("§8§m-----------------------------------------------------")

    val prefixComponent = ChatComponentText("§8[§cRA§8] ")

    fun getAnnounceComponent(playerName: String): ChatComponentText {
        val component = ChatComponentText("§9§l[ANNOUNCE] ")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pc [RA] $playerName is on the dodge list!")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to announce to party chat."))

        return component
    }
    fun getKickComponent(playerName: String): ChatComponentText {
        val uuid = ClickableChatManager.createRunnableAction(
            oneTime = true,
            runnable = {
                ChatUtils.sendMessage("/pc [RA] Kicking $playerName since they are on the dodge list.")
                ravenAddons.launchCoroutine {
                    Thread.sleep(500)

                    ChatUtils.sendMessage("/p kick $playerName")
                }
            }
        ).toString()

        val component = ChatComponentText("§c§l[KICK] ")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ra chat-action $uuid")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to kick the dodged user."))

        return component
    }
    fun getBlockComponent(playerName: String): ChatComponentText {
        val component = ChatComponentText("§7§l[BLOCK] ")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/block add $playerName")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to block the dodged user."))

        return component
    }
    fun getRemoveComponent(playerName: String): ChatComponentText {
        val component = ChatComponentText("§c§l[REMOVE] \n")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ra dodge remove $playerName")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to remove the user from the dodge list."))

        return component
    }
}
