package at.raven.ravenAddons.utils

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.commands.CommandCategory
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.post
import net.minecraft.client.Minecraft
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.network.play.client.C01PacketChatMessage
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ChatUtils {
    val prefixChatComponent = ChatComponentText("§8[§cRA§8] ")

    fun testMessageCommand(array: Array<String>) {
        if (array.isEmpty()) {
            chat("Failed to run because you didn't put a message to test...")
            return
        }
        val hidden = array.last() == "-s"
        var rawMessage = array.toList().joinToString(" ")
        if (!hidden) chat("Testing message: §7$rawMessage")
        if (hidden) rawMessage = rawMessage.replace(" -s", "")
        val formattedMessage = rawMessage.replace("&", "§")

        ChatReceivedEvent(formattedMessage, ChatComponentText(formattedMessage)).post()
        chat(formattedMessage, false)
    }

    fun warning(
        message: String,
        usePrefix: Boolean = true,
    ) {
        val finalMessage = if (usePrefix) "§4[§cRA§4]§7 $message" else message

        chat(finalMessage, usePrefix = false)
    }

    fun chat(
        message: String,
        usePrefix: Boolean = true,
    ) {
        val finalMessage = if (usePrefix) "§8[§cRA§8]§7 $message" else message

        chat(ChatComponentText(finalMessage))
    }

    fun chat(
        nonString: Any,
        usePrefix: Boolean = true,
    ) {
        chat(nonString.toString(), usePrefix)
    }

    fun chat(chatComponent: IChatComponent) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponent)
    }

    fun chatClickable(
        message: String,
        command: String,
        usePrefix: Boolean = true,
    ) {
        val newMessage = if (usePrefix) "§8[§cRA§8]§7 $message" else message

        val text = ChatComponentText(newMessage)
        text.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
        text.chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§eRuns $command"))
        Minecraft.getMinecraft().thePlayer.addChatMessage(text)
    }

    fun sendMessage(message: String) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message)
    }

    fun debug(message: String) {
        if (!ravenAddonsConfig.debugMessages) return

        chat("§7[RA DEBUG] $message", usePrefix = false)
    }

    fun debug(nonString: Any) {
        debug(nonString.toString())
    }

    fun sendChatPacket(packet: C01PacketChatMessage) {
        Minecraft
            .getMinecraft()
            .thePlayer.sendQueue
            .addToSendQueue(packet)
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("testmessage") {
            description = "Prints a message in chat."
            category = CommandCategory.DEVELOPER
            callback { testMessageCommand(it) }
        }
    }

    fun IChatComponent.add(component: IChatComponent) = this.siblings.add(component)
    fun IChatComponent.add(string: String) = this.siblings.add(ChatComponentText(string))
}
