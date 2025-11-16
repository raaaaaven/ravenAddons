package at.raven.ravenAddons.event

import at.raven.ravenAddons.event.base.RavenEvent
import com.google.gson.JsonObject

class ConfigFixEvent(var configJson: JsonObject, private val oldVersion: Int, private val currentVersion: Int): RavenEvent() {
    fun checkVersion(versionToCheck: Int, runnable: () -> Unit): Boolean {
        return if (oldVersion < currentVersion && currentVersion >= versionToCheck) {
            runnable.invoke()
            true
        } else false
    }
}
