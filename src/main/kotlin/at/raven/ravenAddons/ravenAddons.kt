package at.raven.ravenAddons

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.TickEvent
import at.raven.ravenAddons.features.misc.update.UpdateManager
import at.raven.ravenAddons.module.RavenAddonsModules
import at.raven.ravenAddons.utils.ChatUtils
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.owdding.ktmodules.Module
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import kotlin.time.Duration

@Module
@Mod(
    modid = ravenAddons.MOD_ID,
    useMetadata = true,
    version = ravenAddons.MOD_VERSION,
    modLanguageAdapter = "at.raven.ravenAddons.utils.KotlinLanguageAdapter",
)
object ravenAddons {
    private val loadedClasses = mutableSetOf<Any>()

    private fun loadModule(obj: Any) {
        val name = obj.javaClass.name
        require(loadedClasses.add(name)) { "module $name already loaded" }
        MinecraftForge.EVENT_BUS.register(obj)
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        RavenAddonsModules.init(::loadModule)

        CommandRegistrationEvent().post()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        loadedClasses.clear()
    }

    const val MOD_VERSION = "1.11.1"
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
