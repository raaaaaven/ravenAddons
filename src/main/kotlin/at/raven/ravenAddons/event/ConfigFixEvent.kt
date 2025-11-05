package at.raven.ravenAddons.event

import at.raven.ravenAddons.event.base.RavenEvent

class ConfigFixEvent(var configLines: List<String>, private val oldVersion: Int, private val currentVersion: Int): RavenEvent() {
//     fun checkVersion(versionToCheck: Int, runnable: () -> Unit): Boolean {
//         return if (oldVersion < currentVersion && currentVersion <= versionToCheck) {
//             runnable.invoke()
//             true
//         } else false
//     }
//
//     var tomlData: CommentedConfig?
//         get() = TomlParser().parse(configLines.joinToString("\n"))
//         set(value) { configLines = TomlWriter().writeToString(value).split("\n") }
} //todo fix this
