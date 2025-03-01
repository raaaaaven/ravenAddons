package at.raven.ravenAddons.event.chat

import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
class ChatReceivedEvent(val message: String, var chatComponent: IChatComponent) : Event()