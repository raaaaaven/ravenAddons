package at.raven.ravenAddons.features.misc.update

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.ravenAddons.modVersion
import at.raven.ravenAddons.utils.APIUtils.patchHttpsRequest
import at.raven.ravenAddons.utils.ChatUtils
import me.owdding.ktmodules.Module
import moe.nea.libautoupdate.CurrentVersion
import moe.nea.libautoupdate.PotentialUpdate
import moe.nea.libautoupdate.UpdateContext
import moe.nea.libautoupdate.UpdateTarget
import moe.nea.libautoupdate.UpdateUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.concurrent.CompletableFuture
import javax.net.ssl.HttpsURLConnection

@Module
object UpdateManager {
    private val updateContext =
        UpdateContext(
            ModrinthUpdateSource("ravenAddons", "forge", "1.8.9"),
            UpdateTarget.deleteAndSaveInTheSameFolder(this::class.java),
            CurrentVersion.of(modVersion),
            "pre"
        )

    private var activePromise: CompletableFuture<*>? = null
        set(value) {
            field?.cancel(true)
            field = value
        }

    private var potentialUpdate: PotentialUpdate? = null
    private var updateState = UpdateState.NONE

    internal fun modVersionNumber(version: String) = version.removePrefix("ravenAddons ").replace(".", "").toInt()

    private fun checkUpdate(fromCommand: Boolean = false) {
        updateContext.checkUpdate("pre").thenAcceptAsync {
            if (updateState != UpdateState.NONE) return@thenAcceptAsync

            potentialUpdate = it
            if (!it.isUpdateAvailable) {
                if (fromCommand) {
                    ChatUtils.chat("Failed to find an update.")
                } else {
                    ChatUtils.debug("Failed to find an update.")
                }
                return@thenAcceptAsync
            }
            if (modVersionNumber(it.update.versionName) <= modVersion) {
                if (fromCommand) {
                    ChatUtils.chat("ravenAddons ${ravenAddons.MOD_VERSION} is the latest version.")
                } else {
                    ChatUtils.debug("ravenAddons ${ravenAddons.MOD_VERSION} is the latest version.")
                }
                return@thenAcceptAsync
            }
            var message = "${it.update.versionName} is available!"
            if (!(fromCommand || ravenAddonsConfig.fullAutoUpdates)) {
                message += " Use §b/ra update §7to download it."
            }
            ChatUtils.chat(message)

            updateState = UpdateState.AVAILABLE
            if (fromCommand || ravenAddonsConfig.fullAutoUpdates) queueUpdate()
        }
    }

    private fun queueUpdate() {
        updateState = UpdateState.QUEUED
        activePromise =
            CompletableFuture
                .supplyAsync {
                    val update = potentialUpdate!!
                    update.update.versionName
                    ChatUtils.chat("Downloading ${update.update.versionName}.")
                    update.prepareUpdate()
                }.thenAcceptAsync {
                    ChatUtils.chat("Finished downloading. The update will be installed after the next restart.")
                    updateState == UpdateState.DOWNLOADED
                    potentialUpdate!!.executePreparedUpdate()
                }
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("update") {
            description = "Checks for new ravenAddons updates."
            callback { updateCommand() }
        }
    }

    fun updateCommand() {
        when (updateState) {
            UpdateState.NONE -> checkUpdate(true)
            UpdateState.AVAILABLE -> queueUpdate()
            else -> return
        }
    }

    @SubscribeEvent
    fun onHypixelJoin(event: HypixelJoinEvent) {
        if (!ravenAddonsConfig.autoUpdates) return
        checkUpdate()
    }

    init {
        updateContext.cleanup()
        UpdateUtils.patchConnection {
            if (it is HttpsURLConnection) {
                it.patchHttpsRequest()
            }
        }
    }

    enum class UpdateState {
        AVAILABLE,
        QUEUED,
        DOWNLOADED,
        NONE,
    }
}
