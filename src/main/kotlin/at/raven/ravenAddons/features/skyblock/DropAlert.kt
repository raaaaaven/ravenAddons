package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.StringUtils.removeColors
import at.raven.ravenAddons.utils.TitleManager
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object DropAlert {
    private val rngPattern =
        "^(?<type>PRAY TO RNGESUS|INSANE|CRAZY RARE|VERY RARE|RARE|UNCOMMON|PET) DROP! (?<drop>.+)$".toPattern()

    // https://regex101.com/r/W7Bylx/4
    private val dropPattern = "^(?:§r)?+(?<title>(?<dropTypeColor>(?:§.)+)(?<dropType>[\\w ]+[CD]ROP)! (?:(?:§.)?)+(?:\\((?:§.)+(?<multiDropCount>\\d+x)? ?(?:§.)+)?(?<itemColor>§.)(?<item>[^§]*)(?:(?:§.)+\\))?)(?: (?<subtitle>(?:§r§b|§6)\\(.*?\\)(?:§r)?))?(?: §r)?$".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        dropPattern.matchMatcher(event.message) {
            val dropType = group("dropType") ?: return
            val extra = group("subtitle").orEmpty().removeColors()

            val itemName = group("item") ?: return
            val itemColor = group("itemColor") ?: return
            val multiDropCount = group("multiDropCount")
            val item = multiDropCount?.let { "$it $itemName" } ?: itemName

            val title = if (ravenAddonsConfig.dropTitleCategory) (group("title")) else ("$itemColor$itemName")

            ChatUtils.debug("$title ${group("subtitle")}")

            if (ravenAddonsConfig.dropAlert && ravenAddonsConfig.dropAlertUserName.isNotEmpty()) {

                ravenAddons.launchCoroutine {
                    delay(500)

                    val message = buildString {
                        append("/msg ${ravenAddonsConfig.dropAlertUserName} [RA] ")
                        append("$dropType $item")
                        if (extra.isNotEmpty()) {
                            append(" $extra")
                        }
                    }

                    ChatUtils.sendMessage(message)
                }
            }

            val dropTypeColor = group("dropTypeColor") ?: ""

            val configRarity = ItemRarity.entries[ravenAddonsConfig.dropTitleRarity]
            val titleRarity = dropTypeColor.getOrNull(1)?.let { ItemRarity.getFromChatColor(it) } ?: run {
                ChatUtils.warning("Couldn't find color code in drop!")
                println(event.message)
                return
            }

            if (ravenAddonsConfig.dropTitle) {
                if (titleRarity >= configRarity) {
                    TitleManager.setTitle(
                        title,
                        group("subtitle"),
                        3.seconds,
                        1.seconds,
                        1.seconds,
                    )
                }
            }
        }
    }
}