package at.raven.ravenAddons.event

import gg.essential.vigilance.impl.nightconfig.core.CommentedConfig
import gg.essential.vigilance.impl.nightconfig.toml.TomlParser
import gg.essential.vigilance.impl.nightconfig.toml.TomlWriter
import net.minecraftforge.fml.common.eventhandler.Event

class ConfigFixEvent(var configLines: List<String>, private val oldVersion: Int, private val currentVersion: Int): Event() {
    fun checkVersion(versionToCheck: Int, runnable: () -> Unit): Boolean {
        return if (oldVersion < currentVersion && currentVersion <= versionToCheck) {
            runnable.invoke()
            true
        } else false
    }

    var tomlData: CommentedConfig?
        get() = TomlParser().parse(configLines.joinToString("\n"))
        set(value) { configLines = TomlWriter().writeToString(value).split("\n") }
}