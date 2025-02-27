package at.raven.ravenAddons.features.dungeons.dodgelist

import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import sun.audio.AudioPlayer.player

object DodgeListChatComponents {
    fun getLineComponent(withNewLine: Boolean = true) =
        if (withNewLine) ChatComponentText("§8§m-----------------------------------------------------\n")
        else ChatComponentText("§8§m-----------------------------------------------------")

    val prefixComponent = ChatComponentText("§8[§cRA§8] ")

    val announceComponent: ChatComponentText get() {
        val component = ChatComponentText("§9§l[ANNOUNCE] ")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pc [RA] $player is on the dodge list!")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to announce to party chat."))

        return component
    }
    val kickComponent: ChatComponentText get() {
        val component = ChatComponentText("§c§l[KICK] ")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ra-action-kick $player")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to kick the dodged user."))

        return component
    }
    val blockComponent: ChatComponentText get() {
        val component = ChatComponentText("§7§l[BLOCK] ")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/block add $player")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to block the dodged user."))

        return component
    }
    val removeComponent: ChatComponentText get() {
        val component = ChatComponentText("§c§l[REMOVE] \n")
        component.chatStyle.chatClickEvent =
            ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dodge remove $player")
        component.chatStyle.chatHoverEvent =
            HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to remove the user from the dodge list."))

        return component
    }

    fun IChatComponent.add(component: IChatComponent) = this.siblings.add(component)
    fun IChatComponent.add(string: String) = this.siblings.add(ChatComponentText(string))
}