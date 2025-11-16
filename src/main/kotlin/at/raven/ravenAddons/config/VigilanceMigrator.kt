package at.raven.ravenAddons.config

object VigilanceMigrator {
    private val tomlPattern = "^\\t+?\"?(?<name>[\\w_/!]*)?\"? = (?<value>.*)$".toPattern()

//     fun asdf() {
//         val configLines: List<String>
//         try {
//             configLines = ConfigFixer.secondConfigFile.readLines()
//         } catch (e: Exception) {
//             return
//         }
//         val configValues = mutableMapOf<String, KProperty1<ravenAddonsConfig, *>>()
//
//         for (field in ravenAddonsConfig::class.memberProperties) {
//             val configName = (field.javaField?.annotations?.firstOrNull()?.let { it::class.memberProperties.find { prop -> prop.name == "name" }?.call(it) } as? String) ?: continue
//             configValues[configName.replace('_', ' ')] = field
//         }
//
//         val asdf = ravenAddonsConfig::configVersion
//
//         configLines.forEach { line ->
//             tomlPattern.matchMatcher(line) {
//                 val key = group("name").lowercase()
//                 var value: Any? = null
//
//                 val bool_value = group("value").toBooleanStrictOrNull()
//                 if (bool_value != null) {
//                     value = bool_value
//                 } else {
//                     val int_value = group("value").toIntOrNull()
//                     if (int_value != null) {
//                         value = int_value
//                     } else {
//                         val float = group("value").toFloatOrNull()
//                         if (float != null) {
//                             value = float
//                         }
//                     }
//                 }
//
//                 if (value == null) return
//
//
//         }
//     }
}
