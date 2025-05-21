package at.raven.ravenAddons.commands

import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.commands.CommandCategory
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.utils.ChatUtils
import me.owdding.ktmodules.Module
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Module
object RefillCommand {
    private enum class Item(
        val command: String,
        val aliases: List<String>,
        val item: String,
        val id: String,
        val stack: Int,
        val colour: String = "§f",
        val description: String = "Refills your stack of $item to $stack."
    ) {
        PEARL("pearl", listOf("ep"), "Ender Pearl", "ender_pearl", 16),
        JERRY("jerry", listOf("ij"), "Inflatable Jerry", "inflatable_jerry", 64),
        SUPERBBOOM("superboom", listOf("sb"), "Superboom TNT", "superboom_tnt", 64, "§9"),
        LEAP("leaps", listOf("sl"), "Spirit Leap", "spirit_leap", 16, "§9"),
        DECOY("decoy", listOf("de"), "Decoy", "decoy", 64, "§a"),

        COBBLESTONE("cobblestone", listOf("cs"), "Cobblestone", "cobblestone", 64),
        BOB_OMB("bob-omb", listOf("bo"), "Bob-omb", "bob-omb", 64, "§9"),
        OIL_BARREL("barrel", listOf("ob"), "Oil Barrel", "oil_barrel", 64, "§a"),
        GOBLIN_EGG("goblin", listOf("ge"), "Goblin Egg", "goblin_egg", 64, "§9"),
        JUNGLE_KEY("jungle", listOf("jk"), "Jungle Key", "jungle_key", 64, "§5");
    }

    @SubscribeEvent
    fun onCommandRegisteration(event: CommandRegistrationEvent) {
        Item.entries.forEach { item ->
            event.register(item.command) {
                description = item.description
                aliases = item.aliases
                category = CommandCategory.REFILL
                callback { refill(item) }
            }
        }
    }

    private fun refill(item: Item) {
        if (!HypixelGame.inSkyBlock) return

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
            ChatUtils.debug("Found ${item.item} at max amount (${item.stack}) in the inventory.")
            ChatUtils.chat("You already have §f${item.stack} §7${item.item}s in your inventory.")
            return
        }

        ChatUtils.debug("Found " + stack.stackSize + " ${item.item} in the inventory so refilling $amount.")
        ChatUtils.chat("Retrieving §f$amount §7${item.item}s from sack.")
        ChatUtils.sendMessage("/gfs ${item.id} $amount")
    }
}