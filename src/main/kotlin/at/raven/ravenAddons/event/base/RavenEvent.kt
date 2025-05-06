package at.raven.ravenAddons.event.base

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event

abstract class RavenEvent : Event() {
    open fun post(): Boolean {
        return try {
            MinecraftForge.EVENT_BUS.post(this)
            false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}