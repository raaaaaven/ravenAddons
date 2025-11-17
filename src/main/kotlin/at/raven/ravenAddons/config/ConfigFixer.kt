package at.raven.ravenAddons.config

import at.raven.ravenAddons.event.ConfigFixEvent
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.minecraft.crash.CrashReport
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

object ConfigFixer {
    private val firstConfigFile = File("./config/ravenAddons.toml")
    val secondConfigFile = File("./config/ravenAddons/config.toml")
    private val secondConfigPath = File("./config/ravenAddons/")

    // this probably won't work with non-default profiles
    private val configFile = File("./OneConfig/profiles/Default Profile/ravenAddons/config.json")

    private val versionInConfigPattern = "\travenaddonsversion = (?<version>\\d+)".toPattern()

    fun init() {
        moveToVigilancePath()
        VigilanceMigrator.insertVigilanceConfig()
        fixConfigEvent()
    }

    private fun moveToVigilancePath() {
        try {
            secondConfigPath.mkdirs()
            moveConfig()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun moveConfig() {
        if (firstConfigFile.exists() && !secondConfigFile.exists()) {
            firstConfigFile.move(secondConfigFile)
            firstConfigFile.delete()
        }
    }

    private fun checkVigilantVersion(): Boolean {
        try {
            secondConfigFile.readLines().forEach {
                versionInConfigPattern.matchMatcher(it) {
                    if (group("version").toInt() < 1133) {
                        return true
                    }
                }
            }
        } catch (_: Exception) {}
        return false
    }

    private fun fixConfigEvent() {
        val configJson: JsonObject?
        try {
            configJson = JsonParser().parse(configFile.readText()).asJsonObject
        } catch (_: Exception) {
            if (checkVigilantVersion()) {
                ravenAddons.mc.displayCrashReport(CrashReport("bad config, update to the latest vigilant config first", Throwable("bad vigilant config")))
            }
            return
        }
        configJson as JsonObject

        val oldVersion: Int = configJson["configVersion"].asInt

        if (oldVersion >= ravenAddons.modVersion) return
        val event = ConfigFixEvent(configJson, oldVersion, ravenAddons.modVersion)
        event.post()

        configFile.writeText(configJson.toString())
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
