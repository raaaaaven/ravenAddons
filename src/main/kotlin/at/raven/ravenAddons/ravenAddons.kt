package at.raven.ravenAddons

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.loadmodule.LoadedModules
import at.raven.ravenAddons.ravenAddons.Companion.MOD_VERSION
import at.raven.ravenAddons.utils.ChatUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

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
        loadedClasses.clear()
    }

    @LoadModule
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
        private var screenToOpenNextTick: GuiScreen? = null

        fun openScreen(screen: GuiScreen) {
            screenToOpenNextTick = screen
        }

        @SubscribeEvent
        fun onTick(event: TickEvent.ClientTickEvent) {
            if (event.phase != TickEvent.Phase.END) return
            if (screenToOpenNextTick != null) {
                Minecraft.getMinecraft().displayGuiScreen(screenToOpenNextTick)
                screenToOpenNextTick = null
            }
        }
    }
}