package at.raven.ravenAddons.event.base

import net.minecraftforge.common.MinecraftForge

abstract class CancellableRavenEvent : RavenEvent() {
    override fun post(): Boolean {
        MinecraftForge.EVENT_BUS.post(this)
        return this.isCanceled
    }
    fun cancel() {
        this.isCanceled = true
    }
    override fun isCancelable(): Boolean = true
}