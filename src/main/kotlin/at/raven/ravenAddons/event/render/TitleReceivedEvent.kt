package at.raven.ravenAddons.event.render

import net.minecraft.network.play.server.S45PacketTitle
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event

@Cancelable
class TitleReceivedEvent(
    val formattedText: String,
    val component: IChatComponent,
    val type: S45PacketTitle.Type,
) : Event()
