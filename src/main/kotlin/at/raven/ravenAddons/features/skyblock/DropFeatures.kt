package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.StringUtils.removeColors
import at.raven.ravenAddons.utils.TitleManager
import kotlinx.coroutines.delay
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration.Companion.seconds

@LoadModule
object DropFeatures {

    private var titleCooldown = SimpleTimeMark.farPast()
    private var offlineCheck = false

    // https://regex101.com/r/W7Bylx/6
    private val dropPattern = "^(?<title>(?<dropTypeColor>(?:§.)+)(?<dropType>[\\w ]+[CD]ROP)! (?:(?:§.)?)+(?:\\((?:§.)+(?:(?<multiDropColor>§.)(?<multiDropCount>\\d+x) (?:§.)+)?)?(?<itemColor>§.)(?<item>[^§]*)(?:(?:§.)+\\))?)(?:\$| (?:(?<subtitle>(?:§r§b|§6)\\(.*?\\)(?:§r)?))?(?:§r)?\$)".toPattern()

    private val offlinePlayer = "That player is not online!".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (offlineCheck && offlinePlayer.matches(event.message.removeColors())) {
            event.isCanceled = true
            offlineCheck = false
            return
        }

        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        dropPattern.matchMatcher(event.message) {
            val dropType = group("dropType") ?: return
            val extra = group("subtitle").orEmpty().removeColors()

            val itemName = group("item") ?: return
            val itemColor = group("itemColor") ?: ""
            val multiDropCount = group("multiDropCount")
            val multiDropColor = group("multiDropColor")
            val item = multiDropCount?.let { "$it ($itemName)" } ?: itemName

            val title = if (ravenAddonsConfig.dropTitleCategory)
                group("title")
            else {
                buildString {
                if (multiDropCount != null) {
                    append("$multiDropColor(")
                    append(multiDropCount)
                    append(" ")
                    append(itemColor)
                    append(itemName)
                    append("$multiDropColor)")
                } else {
                    append(itemColor)
                    append(itemName)
                    }
                }
            }

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

                    offlineCheck = true
                    ChatUtils.sendMessage(message)

                    delay(2500)
                    offlineCheck = false
                }
            }

            if (ravenAddonsConfig.dropTitle) {

                val configRarity = ItemRarity.entries[ravenAddonsConfig.dropTitleRarity]
                val titleRarity = ItemRarity.runeMap[itemName] ?: itemColor.getOrNull(1)?.let { char -> ItemRarity.getFromChatColor(char) ?: run {
                    ChatUtils.warning("Unknown color code '$char' for rarity!")
                    ItemRarity.COMMON
                } } ?: run {
                    ChatUtils.warning("Couldn't find color code in drop!")
                    println(event.message)
                    return
                }

                if (titleRarity >= configRarity && titleCooldown.isInPast()) {
                    titleCooldown = SimpleTimeMark.now() + 1.seconds
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