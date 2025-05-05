package at.raven.ravenAddons.event.chat

import at.raven.ravenAddons.event.base.CancellableRavenEvent
import at.raven.ravenAddons.utils.StringUtils.removeColors
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.eventhandler.Cancelable

@Cancelable
class ChatReceivedEvent(val message: String, var chatComponent: IChatComponent) : CancellableRavenEvent() {
    val cleanMessage: String = message.removeColors()
}