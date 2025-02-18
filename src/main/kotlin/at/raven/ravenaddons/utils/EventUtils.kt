package at.raven.ravenaddons.utils

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event

object EventUtils {
    fun Event.post() {
        MinecraftForge.EVENT_BUS.post(this)
    }

    fun Event.postAndCatch(): Boolean {
        MinecraftForge.EVENT_BUS.post(this)
        return this.isCanceled
    }
}