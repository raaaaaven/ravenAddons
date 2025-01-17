package at.raven.ravenAddons

import at.raven.ravenAddons.config.ConfigCommand
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadedModules
import at.raven.ravenAddons.ravenAddons.Companion.MOD_VERSION
import at.raven.ravenAddons.utils.ChatUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent

@Suppress("ClassName")
@Mod(modid = "ravenAddons", useMetadata = true, version = MOD_VERSION)
class ravenAddons {
    private val loadedClasses = mutableSetOf<Any>()

    private fun loadModule(obj: Any) {
        if (!loadedClasses.add(obj.javaClass.name)) throw IllegalStateException("module ${obj.javaClass.name} already loaded")
        MinecraftForge.EVENT_BUS.register(obj)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        LoadedModules.modules.forEach { loadModule(it) }

        MinecraftForge.EVENT_BUS.post(CommandRegistrationEvent())
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        ClientCommandHandler.instance.registerCommand(ConfigCommand)
        loadedClasses.clear()
    }

    companion object{
        const val MOD_VERSION = "1.0.0"

        private val globalJob: Job = Job(null)
        val coroutineScope =
            CoroutineScope(
                CoroutineName("ravenAddons") + SupervisorJob(globalJob),
            )

        fun launchCoroutine(function: suspend () -> Unit) {
            coroutineScope.launch {
                try {
                    function()
                } catch (e: Exception) {
                    ChatUtils.warning("Async exception caught")
                    e.printStackTrace()
                }
            }
        }
    }
}