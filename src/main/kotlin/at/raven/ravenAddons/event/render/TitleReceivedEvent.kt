package at.raven.ravenAddons.event.render

import at.raven.ravenAddons.event.base.CancellableRavenEvent
import net.minecraft.network.play.server.S45PacketTitle
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.eventhandler.Cancelable

@Cancelable
class TitleReceivedEvent(
    val formattedText: String,
    val component: IChatComponent,
    val type: S45PacketTitle.Type,
) : CancellableRavenEvent()
