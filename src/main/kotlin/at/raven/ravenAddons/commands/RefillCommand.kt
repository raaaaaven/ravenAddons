package at.raven.ravenAddons.commands

import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object RefillCommand {
    @SubscribeEvent
    fun onCommandRegisteration(event: CommandRegistrationEvent) {
        event.register("ep") {
            description = "Refill your stack of Ender Pearls to 16."
            callback { refill("Ender Pearl", "ender_pearl", 16, "§f") }
        }

        event.register("ij") {
            description = "Refill your stack of Inflatable Jerrys to 64."
            callback { refill("Inflatable Jerry", "inflatable_jerry", 64, "§f") }
        }

        event.register("sb") {
            description = "Refill your stack of Superboom TNT to 64."
            callback { refill("Superboom TNT", "superboom_tnt", 64, "§9") }
        }

        event.register("sl") {
            description = "Refill your stack of Spirit Leaps to 16."
            callback { refill("Spirit Leap", "spirit_leap", 16, "§9") }
        }

        event.register("de") {
            description = "Refill your stack of Decoys to 64."
            callback { refill("Decoy", "decoy", 64, "§a") }
        }
    }

    private fun refill(name: String, id: String, max: Int, colour: String) {
        val inventory = Minecraft.getMinecraft().thePlayer?.inventory?.mainInventory ?: return
        val item = inventory.find { it?.displayName == "$colour$name" }
        val amount = max - (item?.stackSize ?: 0)

        if (item == null) {
            ChatUtils.debug("No stack found for $name so grabbing $max.")
            ChatUtils.chat("Grabbing §f$max §7${name}s from sack.")
            ChatUtils.sendMessage("/gfs $id $max")
            return
        }

        if (amount == 1) {
            ChatUtils.debug("Found " + item.stackSize + " $name in the inventory so refilling $amount.")
            ChatUtils.chat("Grabbing §f$amount §7$name from sack.")
            ChatUtils.sendMessage("/gfs $id $amount")
        }

        if (amount == 0) {
            ChatUtils.debug("Found max amount ($max) in the inventory.")
            ChatUtils.chat("You already have §f$max §7${name}s in your inventory.")
            return
        }

        ChatUtils.debug("Found " + item.stackSize + " $name in the inventory so refilling $amount.")
        ChatUtils.chat("Grabbing §f$amount §7${name}s from sack.")
        ChatUtils.sendMessage("/gfs $id $amount")
    }
}