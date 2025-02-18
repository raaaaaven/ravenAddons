package at.raven.ravenaddons

import at.raven.ravenaddons.RavenAddons.Companion.MOD_ID
import at.raven.ravenaddons.RavenAddons.Companion.MOD_VERSION
import at.raven.ravenaddons.event.CommandRegistrationEvent
import at.raven.ravenaddons.loadmodule.LoadModule
import at.raven.ravenaddons.loadmodule.LoadedModules
import at.raven.ravenaddons.utils.ChatUtils
import at.raven.ravenaddons.utils.EventUtils.post
import kotlinx.coroutines.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@Mod(modid = MOD_ID, useMetadata = true, version = MOD_VERSION)
class RavenAddons {
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
        const val MOD_VERSION = "1.1.1"
        const val MOD_ID = "ravenAddons"

        val mc: Minecraft get() = Minecraft.getMinecraft()

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