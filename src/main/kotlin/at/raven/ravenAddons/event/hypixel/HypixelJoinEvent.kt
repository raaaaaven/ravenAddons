package at.raven.ravenAddons.event.hypixel

import net.hypixel.data.region.Environment
import net.minecraftforge.fml.common.eventhandler.Event

class HypixelJoinEvent(
    val type: Environment,
) : Event()
