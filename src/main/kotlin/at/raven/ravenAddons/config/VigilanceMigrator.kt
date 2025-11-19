package at.raven.ravenAddons.config

import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import cc.polyfrost.oneconfig.config.core.OneColor
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

@LoadModule
object VigilanceMigrator {
    private val tomlPattern = "^\\t+?\"?(?<name>[\\w_/!]*)?\"? = (?<value>.*)$".toPattern()

    private val badValues = mutableListOf<String>()
    private var printedMessage = true

    @SubscribeEvent
    fun onHypixelJoin(event: HypixelJoinEvent) {
        if (printedMessage) return

        ChatUtils.chat("couldn't migrate these values $badValues")
    }

    private fun getConfigMap(): Map<String, KProperty1<ravenAddonsConfig, *>> {
        return ravenAddonsConfig::class.memberProperties.associateBy { field ->
            val configName = field.javaField?.annotations?.firstNotNullOfOrNull { annotation ->
                runCatching {
                    val nameMethod = annotation.annotationClass.java.getMethod("name")
                    nameMethod.invoke(annotation) as? String
                }.getOrNull()
            }
            val configNameFallback = configName ?: run {
                when (field) {
                    ravenAddonsConfig::configVersion -> "ravenaddonsversion"
                    ravenAddonsConfig::sinceInq -> "sinceinq"

                    else -> field.name.replace("(?<=[a-z])([A-Z\\d])".toRegex(), "_$1").lowercase()
                }
            }

            (configNameFallback.trim().lowercase().replace(' ', '_'))
        }
    }

    private fun getVigilanceLines(): List<String>? {
        return try {
            ConfigFixer.secondConfigFile.readLines()
        } catch (_: Exception) {
            null
        }
    }

    fun insertVigilanceConfig() {
        val configLines = getVigilanceLines() ?: return
        val configValues = getConfigMap()

        loop@ for (line in configLines) {
            tomlPattern.matchMatcher(line) {
                val key = group("name").lowercase()
                val value: Any
                group("value").let { groupValue ->
                    value = groupValue.attemptConversion() ?: run {
                        groupValue.removePrefix("\"").removeSuffix("\"")
                    }
                }

                @Suppress("UNCHECKED_CAST")
                val mutableField = configValues.getOrElse(key) {
                    badValues.add(key)
                    printedMessage = false
                    continue@loop
                } as KMutableProperty1<ravenAddonsConfig, Any>

                mutableField.set(ravenAddons.config, value)
            }
        }
    }

    private fun String.attemptConversion(): Any? = this.toBooleanStrictOrNull() ?: this.toIntOrNull() ?: this.toFloatOrNull() ?: this.toOneColorOrNull()

    private fun String?.toOneColorOrNull(): OneColor? {
        val newString = this?.removePrefix("\"")?.removeSuffix("\"") ?: return null
        val arr = newString.split(',')

        if (arr.size != 4) return null

        return try {
            OneColor(arr[0].toInt(), arr[1].toInt(), arr[2].toInt(), arr[3].toInt())
        } catch (_: NumberFormatException) {
            null
        }
    }
}
