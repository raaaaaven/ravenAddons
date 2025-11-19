package at.raven.ravenAddons.config

import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import cc.polyfrost.oneconfig.events.EventManager
import cc.polyfrost.oneconfig.events.event.InitializationEvent
import cc.polyfrost.oneconfig.libs.eventbus.Subscribe
import kotlinx.coroutines.delay
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object ConfigManager {
    lateinit var config: ravenAddonsConfig
    private var wasModUpdated = ModUpdateStatus.NONE
    private var updateMessageSent = false

    init {
        EventManager.INSTANCE.register(this)
    }

    @Subscribe
    fun onOneConfigInit(event: InitializationEvent) {
        initConfig()
    }

    @SubscribeEvent
    fun onHypixelJoin(event: HypixelJoinEvent) {
        if (!updateMessageSent) {
            wasModUpdated.sendMessage()
            updateMessageSent = true
        }
    }

    private fun initConfig() {
        ConfigFixer.fixConfigEvent()
        config = ravenAddonsConfig()
        ConfigFixer.attemptVigilanceMigration()

        if (ravenAddons.config.configVersion < ravenAddons.modVersion) {
            ravenAddons.config.configVersion = ravenAddons.modVersion
            wasModUpdated = ModUpdateStatus.UPDATED
        } else if (ravenAddons.config.configVersion > ravenAddons.modVersion) {
            ravenAddons.config.configVersion = ravenAddons.modVersion
            wasModUpdated = ModUpdateStatus.DOWNGRADED
        }
        ravenAddons.config.save()
    }

    fun openConfig() {
        config.openGui()
    }

    enum class ModUpdateStatus(
        private val componentMessage: ChatComponentText? = null,
        private val stringMessage: String? = null,
        val warning: Boolean = false
    ) {
        NONE,
        UPDATED(
            componentMessage = run {
                val finalComponent = ChatComponentText("§8[§cRA§8] §7ravenAddons successfully updated to version ${ravenAddons.MOD_VERSION}!\n")
                val linkComponent =
                    ChatComponentText("§8[§cRA§8] §7Click here to open the changelog on Modrinth.")
                linkComponent.chatStyle.chatHoverEvent =
                    HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("Click here to open the changelog!"))
                linkComponent.chatStyle.chatClickEvent =
                    ClickEvent(
                        ClickEvent.Action.OPEN_URL,
                        "https://modrinth.com/mod/ravenaddons/version/tag/${ravenAddons.MOD_VERSION}"
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
}
