package at.raven.ravenAddons.event

import net.minecraftforge.fml.common.eventhandler.Event

class ConfigFixEvent(var configLines: List<String>, private val oldVersion: Int, private val newVersion: Int): Event() {
    fun checkVersion(version: Int, runnable: () -> Unit): Boolean {
        return if (oldVersion < version && version <= newVersion) {
            runnable.invoke()
            true
        } else false
    }
}