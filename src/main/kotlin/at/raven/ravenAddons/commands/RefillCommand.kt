package at.raven.ravenAddons.commands

import at.raven.ravenAddons.data.commands.CommandCategory
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.utils.ChatUtils
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@LoadModule
object RefillCommand {
    private enum class items(
        val command: String,
        val aliases: List<String>,
        val descrption: String,
        val item: String,
        val id: String,
        val stack: Int,
        val colour: String,
    ) {
        PEARL("pearl", listOf("ep"), "Refill your stack of Ender Pearls to 16.", "Ender Pearl", "ender_pearl", 16, "§f"),
        JERRY("jerry", listOf("ij"), "Refill your stack of Inflatable Jerrys to 64.", "Inflatable Jerry", "inflatable_jerry", 64, "§f"),
        SUPERBBOOM("superboom", listOf("sb"), "Refill your stack of Superboom TNT to 64.", "Superboom TNT", "superboom_tnt", 64, "§9"),
        LEAP("leaps", listOf("sl"), "Refill your stack of Spirit Leaps to 16.", "Spirit Leap", "spirit_leap", 16, "§9"),
        DECOY("decoy", listOf("de"), "Refill your stack of Decoys to 64.", "Decoy", "decoy", 64, "§a"),

        COBBLESTONE("cobblestone", listOf("cs"), "Refill your stack of Cobblestones to 64.", "Cobblestone", "cobblestone", 64, "§f"),
        BOB_OMB("bob-omb", listOf("bo"), "Refill your stack of Bob-ombs to 64.", "Bob-omb", "bob-omb", 64, "§9"),
        OIL_BARREL("barrel", listOf("ob"), "Refill your stack of Oil Barrels to 64", "Oil Barrel", "oil_barrel", 64, "§a")
    }

    @SubscribeEvent
    fun onCommandRegisteration(event: CommandRegistrationEvent) {
        items.entries.forEach { item ->
            event.register(item.command) {
                description = item.descrption
                aliases = item.aliases
                category = CommandCategory.REFILL
                callback { refill(item) }
            }
        }
    }

    private fun refill(item: items) {
        val inventory = Minecraft.getMinecraft().thePlayer?.inventory?.mainInventory ?: return
        val stack = inventory.find { it?.displayName == "${item.colour}${item.item}" }
        val amount = item.stack - (stack?.stackSize ?: 0)

        if (stack == null) {
            ChatUtils.debug("No stack found for ${item.item} so retrieving ${item.stack}.")
            ChatUtils.chat("Retrieving §f${item.stack} §7${item.item}s from sack.")
            ChatUtils.sendMessage("/gfs ${item.id} ${item.stack}")
            return
        }

        if (amount == 1) {
            ChatUtils.debug("Found " + stack.stackSize + " ${item.item} in the inventory so refilling $amount.")
            ChatUtils.chat("Retrieving §f$amount §7${item.item} from sack.")
            ChatUtils.sendMessage("/gfs ${item.id} $amount")
            return
        }

        if (amount == 0) {
            ChatUtils.debug("${item.item} found: max amount (${item.stack}) in the inventory.")
            ChatUtils.chat("You already have §f${item.stack} §7${item.item}s in your inventory.")
            return
        }

        ChatUtils.debug("Found " + stack.stackSize + " ${item.item} in the inventory so refilling $amount.")
        ChatUtils.chat("Retrieving §f$amount §7${item.item}s from sack.")
        ChatUtils.sendMessage("/gfs ${item.id} $amount")
    }
}