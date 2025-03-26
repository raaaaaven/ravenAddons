package at.raven.ravenAddons.commands

import at.raven.ravenAddons.data.commands.CommandCategory
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
            aliases = listOf("pearl")
            category = CommandCategory.REFILL
            callback { refill("Ender Pearl", "ender_pearl", 16, "§f") }
        }

        event.register("jerry") {
            description = "Refill your stack of Inflatable Jerrys to 64."
            aliases = listOf("ij")
            category = CommandCategory.REFILL
            callback { refill("Inflatable Jerry", "inflatable_jerry", 64, "§f") }
        }

        event.register("superboom") {
            description = "Refill your stack of Superboom TNT to 64."
            aliases = listOf("sb")
            category = CommandCategory.REFILL
            callback { refill("Superboom TNT", "superboom_tnt", 64, "§9") }
        }

        event.register("leaps") {
            description = "Refill your stack of Spirit Leaps to 16."
            aliases = listOf("sl")
            category = CommandCategory.REFILL
            callback { refill("Spirit Leap", "spirit_leap", 16, "§9") }
        }

        event.register("decoy") {
            description = "Refill your stack of Decoys to 64."
            aliases = listOf("de")
            category = CommandCategory.REFILL
            callback { refill("Decoy", "decoy", 64, "§a") }
        }

        event.register("cobblestone") {
            description = "Refill your stack of Cobblestones to 64."
            aliases = listOf("cs")
            category = CommandCategory.REFILL
            callback { refill("Cobblestone", "cobblestone", 64, "§f")}
        }

        event.register("bob-omb") {
            description = "Refill your stack of Bob-ombs to 64."
            aliases = listOf("bo")
            category = CommandCategory.REFILL
            callback { refill("Bob-omb", "bob-omb", 64, "§9") }
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