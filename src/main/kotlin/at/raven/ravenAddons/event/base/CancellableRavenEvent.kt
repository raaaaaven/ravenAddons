package at.raven.ravenAddons.event.base

import net.minecraftforge.common.MinecraftForge

abstract class CancellableRavenEvent : RavenEvent() {
    override fun post(): Boolean {
        return try {
            MinecraftForge.EVENT_BUS.post(this)
            this.isCanceled
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    fun cancel() {
        this.isCanceled = true
    }
    override fun isCancelable(): Boolean = true
}