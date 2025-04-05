package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.TitleManager
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import kotlin.time.Duration.Companion.seconds

@LoadModule
object DropAlert {
    private val dropPattern =
        "(?:§r)*(?<dropCategoryColor>§.)(?:§.)+(?<dropCategory>[\\w\\s]*[CD]ROP!) (?:§.)+(?:\\()?(?:§.)*(?:\\d+x )?(?:§.)*(?<dropColor>§.)(?<name>◆?[\\s\\w]+)(?:§.)+\\)? ?(?:(?:§.)+)?(?:\\((?:\\+(?:§.)*(?<magicFind>\\d+)% (?:§.)+✯ Magic Find(?:§.)*|[\\w\\s]+)\\))?".toPattern()

    enum class rarities(
        val colorCode: Char,
    ) {
        COMMON('f'),
        UNCOMMON('a'),
        RARE('2'),
        EPIC('5'),
        LEGENDARY('6'),
        MYTHIC('d')
        ;

        companion object {
            fun getFromChatColor(colorCode: Char) = entries.first { it.colorCode == colorCode }
        }
    }

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return

        dropPattern.matchMatcher(event.message) {
            val dropCategoryColor = group("dropCategoryColor")
            val dropCategory = group("dropCategory")
            val dropColor = group("dropColor")
            val name = group("name")
            val magicFind = group("magicFind")

            val configRarity = rarities.entries[ravenAddonsConfig.dropTitleRarity]
            val titleRarity = rarities.getFromChatColor(dropColor.last())

            if (ravenAddonsConfig.dropAlert || ravenAddonsConfig.dropAlertUserName.isNotEmpty()) {
                ChatUtils.debug("dropAlert triggered: $dropCategory $name")

                ravenAddons.launchCoroutine {
                    delay(500)

                    if (magicFind.isNullOrEmpty()) {
                        ChatUtils.sendMessage("/msg ${ravenAddonsConfig.dropAlertUserName} [RA] $dropCategory $name")
                    } else {
                        ChatUtils.sendMessage("/msg ${ravenAddonsConfig.dropAlertUserName} [RA] $dropCategory $name(+$magicFind% ✯ Magic Find)")
                    }
                }

                if (ravenAddonsConfig.dropTitle && titleRarity >= configRarity) {

                    TitleManager.setTitle(
                        "$dropCategoryColor§l$dropCategory §r$dropColor$name",
                        "§r§b(+$magicFind% §r§b✯ Magic Find§r§b)",
                        3.seconds,
                        1.seconds,
                        1.seconds
                    )
                }
            }
        }
    }
}