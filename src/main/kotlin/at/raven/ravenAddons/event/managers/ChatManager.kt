package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.chat.ActionBarEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.StringUtils.cleanupColors
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ChatManager {
    @SubscribeEvent(receiveCanceled = true)
    fun onChat(event: ClientChatReceivedEvent) {
        val original = event.message
        val message = original.formattedText.cleanupColors()

        val newEvent =
            if (event.type.toInt() == 2) ActionBarEvent(message, original)
            else ChatReceivedEvent(message, original)

        if (newEvent.post()) event.isCanceled = true
    }
}