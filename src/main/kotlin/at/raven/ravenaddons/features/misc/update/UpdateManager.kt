package at.raven.ravenaddons.features.misc.update

import at.raven.ravenaddons.RavenAddons
import at.raven.ravenaddons.config.RavenAddonsConfig
import at.raven.ravenaddons.event.CommandRegistrationEvent
import at.raven.ravenaddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenaddons.loadmodule.LoadModule
import at.raven.ravenaddons.utils.ChatUtils
import moe.nea.libautoupdate.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.concurrent.CompletableFuture

@LoadModule
object UpdateManager {
    private val updateContext =
        UpdateContext(
            UpdateSource.githubUpdateSource("raaaaaven", "ravenAddons"),
            UpdateTarget.deleteAndSaveInTheSameFolder(this::class.java),
            CurrentVersion.of(modVersion),
            "pre"
        )

    private var _activePromise: CompletableFuture<*>? = null
    private var activePromise: CompletableFuture<*>?
        get() = _activePromise
        set(value) {
            _activePromise?.cancel(true)
            _activePromise = value
        }

    private var potentialUpdate: PotentialUpdate? = null
    private var updateState = UpdateState.NONE

    private fun modVersionNumber(version: String) = version.removePrefix("ravenAddons ").replace(".", "").toInt()
    private val modVersion get() = modVersionNumber(RavenAddons.MOD_VERSION)

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
                    ChatUtils.chat("Already up-to-date.")
                } else {
                    ChatUtils.debug("Already up-to-date.")
                }
                return@thenAcceptAsync
            }
            var message = "${it.update.versionName} is available!"
            if (!(fromCommand || RavenAddonsConfig.fullAutoUpdates)) {
                message += " Use §b/raupdate §7to download it."
            }
            ChatUtils.chat(message)

            updateState = UpdateState.AVAILABLE
            if (fromCommand || RavenAddonsConfig.fullAutoUpdates) queueUpdate()
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
        event.register("raupdate") {
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
        if (!RavenAddonsConfig.autoUpdates) return
        checkUpdate()
    }

    init {
        updateContext.cleanup()
    }

    enum class UpdateState {
        AVAILABLE,
        QUEUED,
        DOWNLOADED,
        NONE,
    }
}