package at.raven.ravenAddons.config

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.GameLoadEvent
import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import kotlinx.coroutines.delay
import net.minecraft.client.gui.GuiScreen
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ConfigCommand {
    private var configGui: GuiScreen? = null
    private var wasModUpdated = ModUpdateStatus.NONE

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("ravenaddons") {
            description = "Opens the Config GUI"
            aliases = listOf("raven", "ra")
            callback { openConfig() }
        }
    }

    @SubscribeEvent
    fun onGameLoad(event: GameLoadEvent) {
        ConfigFixer
        configGui = ravenAddonsConfig.gui()

        if (ravenAddonsConfig.configVersion < ravenAddons.modVersion) {
            ravenAddonsConfig.configVersion = ravenAddons.modVersion
            ravenAddonsConfig.markDirty()
            wasModUpdated = ModUpdateStatus.UPDATED
        } else if (ravenAddonsConfig.configVersion > ravenAddons.modVersion) {
            ravenAddonsConfig.configVersion = ravenAddons.modVersion
            ravenAddonsConfig.markDirty()
            wasModUpdated = ModUpdateStatus.DOWNGRADED
        }
    }

    @SubscribeEvent
    fun onHypixelJoin(event: HypixelJoinEvent) {
        wasModUpdated.sendMessage()
    }

    private fun openConfig() {
        val gui = configGui ?: ravenAddonsConfig.gui() ?: return
        ravenAddons.openScreen(gui)
    }
}
enum class ModUpdateStatus(
    private val componentMessage: ChatComponentText? = null,
    private val stringMessage: String? = null,
    val warning: Boolean = false
) {
    NONE,
    UPDATED(
        componentMessage = run {
            val finalComponent = ChatComponentText("")
            finalComponent.add("§8[§cRA§8] ")
            finalComponent.add("§7ravenAddons successfully updated to version ${ravenAddons.MOD_VERSION}!\n")
            val linkComponent =
                ChatComponentText("§8[§cRA§8] §7Click here to open the changelog on GitHub.")
            linkComponent.chatStyle.chatHoverEvent =
                HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("Click here to open the changelog!"))
            linkComponent.chatStyle.chatClickEvent =
                ClickEvent(
                    ClickEvent.Action.OPEN_URL,
                    "https://github.com/raaaaaven/ravenAddons/releases/tag/${ravenAddons.MOD_VERSION}"
                )
            finalComponent.add(linkComponent)
            finalComponent
        }
    ),
    DOWNGRADED(
        stringMessage = "ravenAddons was downgraded to ${ravenAddons.MOD_VERSION}! May cause issues!",
        warning = true
    ),
    ;

    fun sendMessage() {
        if (this == NONE) return

        ravenAddons.launchCoroutine {
            delay(1000)

            componentMessage?.let { ChatUtils.chat(it); return@launchCoroutine }
            stringMessage?.let { ChatUtils.chat(it); return@launchCoroutine }
        }
    }
}