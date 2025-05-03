package at.raven.ravenAddons.event.chat

import at.raven.ravenAddons.event.base.CancellableRavenEvent
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.eventhandler.Cancelable

@Cancelable
class ActionBarEvent(val message: String, var chatComponent: IChatComponent) : CancellableRavenEvent()