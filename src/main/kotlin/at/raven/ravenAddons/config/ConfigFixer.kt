package at.raven.ravenAddons.config

import at.raven.ravenAddons.event.ConfigFixEvent
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.EventUtils.post
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object ConfigFixer {
    private val oldConfigFile = File("./config/ravenAddons.toml")

    private val configPath = File("config/ravenAddons")
    val configFile = File("config/ravenAddons/config.toml")

    private val versionInConfigPattern = "\travenaddonsversion = (?<version>\\d+)".toPattern()

    init {
        try {
            configPath.mkdirs()
            moveConfig()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (configFile.exists()) fixConfigEvent()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun moveConfig() {
        if (oldConfigFile.exists() && !configFile.exists()) {
            oldConfigFile.move(configFile)
            oldConfigFile.delete()
        }
    }

    private fun fixConfigEvent() {
        val configLines = configFile.readLines()
        val versionLine = configLines.firstOrNull { it.contains("ravenaddonsversion") } ?: ""
        var oldVersion = 0
        versionInConfigPattern.matchMatcher(versionLine) {
            oldVersion = group("version").toInt()
        }

        if (oldVersion >= ravenAddons.modVersion) return
        val event = ConfigFixEvent(configLines, oldVersion, ravenAddons.modVersion)
        event.post()

        val newLines = event.configLines
        configFile.writeText(newLines.joinToString("\n"))
    }

    private fun File.move(path: Path) {
        Files.move(
            this.toPath(),
            path,
            StandardCopyOption.REPLACE_EXISTING,
            StandardCopyOption.ATOMIC_MOVE,
        )
    }

    private fun File.move(file: File) {
        this.move(file.toPath())
    }
}