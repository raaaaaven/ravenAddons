package at.raven.ravenAddons.features.misc.update

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.hypixel.HypixelJoinEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import moe.nea.libautoupdate.CurrentVersion
import moe.nea.libautoupdate.PotentialUpdate
import moe.nea.libautoupdate.UpdateContext
import moe.nea.libautoupdate.UpdateSource
import moe.nea.libautoupdate.UpdateTarget
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.concurrent.CompletableFuture

@LoadModule
object UpdateManager {
    private val updateContext =
        UpdateContext(
            UpdateSource.githubUpdateSource("", "ravenAddons"),
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

    private fun modVersionNumber(version: String) = version.replace(".", "").toInt()
    private val modVersion get() = modVersionNumber(ravenAddons.MOD_VERSION)

    private fun checkUpdate(fromCommand: Boolean = false) {
        updateContext.checkUpdate("pre").thenAcceptAsync {
            if (updateState != UpdateState.NONE) return@thenAcceptAsync

            potentialUpdate = it
            if (!it.isUpdateAvailable) {
                if (fromCommand) {
                    ChatUtils.chat("Didn't find any updates")
                } else {
                    ChatUtils.debug("did not find an update")
                }
                return@thenAcceptAsync
            }
            if (modVersionNumber(it.update.versionName) <= modVersion) {
                if (fromCommand) {
                    ChatUtils.chat("Already up-to-date")
                } else {
                    ChatUtils.debug("already up-to-date")
                }
                return@thenAcceptAsync
            }
            ChatUtils.chat("§aFound update ${it.update.versionName}! Use §b/raupdate §ato complete it.")
            ChatUtils.debug("${it.update.versionNumber.asNumber}")
            updateState = UpdateState.AVAILABLE
//            if (config.fullAutoUpdates) queueUpdate()
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
            description = "Checks for new ravenAddons updates"
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