package at.raven.ravenAddons

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.TickEvent
import at.raven.ravenAddons.features.misc.update.UpdateManager
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.loadmodule.LoadedModules
import at.raven.ravenAddons.ravenAddons.Companion.MOD_ID
import at.raven.ravenAddons.ravenAddons.Companion.MOD_VERSION
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.EventUtils.post
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration

@Mod(modid = MOD_ID, useMetadata = true, version = MOD_VERSION)
class ravenAddons {
    private val loadedClasses = mutableSetOf<Any>()

    private fun loadModule(obj: Any) {
        if (!loadedClasses.add(obj.javaClass.name)) throw IllegalStateException("module ${obj.javaClass.name} already loaded")
        MinecraftForge.EVENT_BUS.register(obj)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        LoadedModules.modules.forEach { loadModule(it) }

        CommandRegistrationEvent().post()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        loadedClasses.clear()
    }

    @LoadModule
    companion object{
        const val MOD_VERSION = "1.9.0"
        const val MOD_ID = "ravenAddons"
        val modVersion get() = UpdateManager.modVersionNumber(MOD_VERSION)

        val mc get() = Minecraft.getMinecraft()
        private val globalJob: Job = Job(null)

        val coroutineScope =
            CoroutineScope(
                CoroutineName("ravenAddons") + SupervisorJob(globalJob),
            )

        fun runDelayed(delay: Duration, function: suspend () -> Unit) {
            launchCoroutine {
                delay(delay)
                function()
            }
        }

        fun launchCoroutine(function: suspend () -> Unit): Job {
            return coroutineScope.launch {
                try {
                    function()
                } catch (e: Exception) {
                    ChatUtils.warning("Caught a ${e.javaClass.simpleName} exception.")
                    e.printStackTrace()
                }
            }
        }

        private var screenToOpenNextTick: GuiScreen? = null

        fun openScreen(screen: GuiScreen) {
            screenToOpenNextTick = screen
        }

        @SubscribeEvent
        fun onTick(event: TickEvent) {
            if (screenToOpenNextTick != null) {
                Minecraft.getMinecraft().displayGuiScreen(screenToOpenNextTick)
                screenToOpenNextTick = null
            }
        }
    }
}