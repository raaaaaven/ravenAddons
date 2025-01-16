package at.raven.ravenAddons.config

import at.raven.ravenAddons.loadmodule.LoadModule
import gg.essential.universal.UChat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

@LoadModule
object ConfigCommand: CommandBase() {
    var screenToOpenNextTick: GuiScreen? = null;

    override fun getCommandName() = "ravenAddons"
    override fun getCommandUsage(sender: ICommandSender?) = ""
    override fun canCommandSenderUseCommand(sender: ICommandSender?) = true
    override fun getCommandAliases(): List<String?>? {
        return listOf("raven", "ra")
    }

    override fun processCommand(sender: ICommandSender?, args: Array<out String?>?) {
        val gui = ravenAddonsConfig.gui() ?: run {
            UChat.chat("Failed to open config gui")
            return
        }
        screenToOpenNextTick = gui
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