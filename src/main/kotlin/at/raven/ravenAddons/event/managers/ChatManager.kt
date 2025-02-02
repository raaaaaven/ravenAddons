package at.raven.ravenAddons.event.managers

import at.raven.ravenAddons.event.chat.ActionBarEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.EventUtils.cancel
import at.raven.ravenAddons.utils.EventUtils.postAndCatch
import at.raven.ravenAddons.utils.StringUtils.cleanupColors
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ChatManager {
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        val original = event.message
        var message = original.formattedText

        message = message.cleanupColors()

        if (event.type.toInt() != 2) {
            val isCancelled = ChatReceivedEvent(message, original).postAndCatch()

            if (isCancelled) event.cancel()
        } else {
            val isCancelled = ActionBarEvent(message, original).postAndCatch()

            if (isCancelled) event.cancel()
        }
    }
}