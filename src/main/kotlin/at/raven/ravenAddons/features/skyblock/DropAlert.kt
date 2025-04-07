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
    private val dropPattern = "^(?<dropCategory>[\\w\\s]+[CD]ROP!) +\\(?(?<name>[◆\\w ]+)\\)?(?:$| \\((?<dropType>Armor Set Bonus|\\+[\\d,.]+(?:% ✯ Magic Find|☘))\\))".toPattern()

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        dropPattern.matchMatcher(event.message.removeColors()) {
            val coloredMessage = event.message

            val dropCategory = group("dropCategory")
            val dropCategoryColor = coloredMessage.substring(0..<coloredMessage.indexOf(dropCategory))

            val name = group("name")
            val nameIndex = coloredMessage.indexOf(name)
            val nameColor = coloredMessage.substring(nameIndex-2 ,nameIndex)

            val dropType = group("dropType")
            val dropTypeIndex = coloredMessage.indexOf(dropType)
            val dropTypeColor = coloredMessage.substring(dropTypeIndex-2 ,dropTypeIndex)

            val configRarity = ItemRarity.entries[ItemRarity.UNCOMMON.ordinal]
            val titleRarity = ItemRarity.getFromChatColor(nameColor.last())

            ChatUtils.debug("item is $titleRarity")

            if (ravenAddonsConfig.dropAlert || ravenAddonsConfig.dropAlertUserName.isNotEmpty()) {
                ChatUtils.debug("Drop Alert: $dropCategory $name $dropType")
                ravenAddons.Companion.launchCoroutine {
                    delay(500)
                    if (dropType != null) {
                        ChatUtils.sendMessage("/msg ${ravenAddonsConfig.dropAlertUserName} [RA] $dropCategory $name $dropType") } else
                        (ChatUtils.sendMessage("/msg ${ravenAddonsConfig.dropAlertUserName} [RA] $dropCategory $name"))
                }
            }
            if (titleRarity >= configRarity) {

            val subtitle = if (dropType != null) {
                "$dropTypeColor($dropType)"
            } else ""

            ChatUtils.debug("Drop Title: $dropCategoryColor$dropCategory $nameColor$name $dropType".replace('§','&'))
            TitleManager.setTitle(
                "$dropCategoryColor$dropCategory §r$nameColor$name",
                subtitle,
                3.seconds,
                1.seconds,
                1.seconds)
            }
        }
    }
}