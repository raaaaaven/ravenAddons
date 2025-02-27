package at.raven.ravenAddons.features.dungeons.dodgelist

import at.raven.ravenAddons.data.PartyAPI
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents.announceComponent
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents.blockComponent
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents.kickComponent
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents.prefixComponent
import at.raven.ravenAddons.features.dungeons.dodgelist.DodgeListChatComponents.removeComponent
import at.raven.ravenAddons.features.dungeons.dodgelist.subcommands.DodgeListAdd
import at.raven.ravenAddons.features.dungeons.dodgelist.subcommands.DodgeListHelp
import at.raven.ravenAddons.features.dungeons.dodgelist.subcommands.DodgeListList
import at.raven.ravenAddons.features.dungeons.dodgelist.subcommands.DodgeListRemove
import at.raven.ravenAddons.features.dungeons.dodgelist.subcommands.DodgeListReset
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.PlayerUtils.getPlayer
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.SoundUtils
import at.raven.ravenAddons.utils.StringUtils.cleanupColors
import at.raven.ravenAddons.utils.TitleManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.reflect.TypeToken
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.File
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@LoadModule
object DodgeList {
    private val fullPartyPattern =
        "Party Finder > Your (dungeon|kuudra) group is full!.+".toPattern()
    private val playerJoinPattern =
        "Party Finder > (?<name>.+) joined.+".toPattern()

    private val configPath = File("config/ravenAddons")
    private val configFile = File(configPath, "dodgeList.json")

    // uuid + (name, reason)
    internal val throwers: MutableMap<UUID, DodgeListCustomData> = mutableMapOf()

    init {
        loadFromFile()
    }

    val subcommands = listOf<DodgeListSubcommand>(
        DodgeListAdd,
        DodgeListRemove,
        DodgeListList,
        DodgeListReset,
        DodgeListHelp,
    )

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (false /* todo: config */) return

        if (fullPartyPattern.matches(event.message.cleanupColors())) {

            ChatUtils.chat("Checking for people in the dodge list...")
            PartyAPI.sendPartyPacket()
        }
        ravenAddons.launchCoroutine {
            playerJoinPattern.matchMatcher(event.message.cleanupColors()) {
                val player = group("name")

                if (player == PlayerUtils.playerName) {
                    PartyAPI.sendPartyPacket()
                    return@launchCoroutine
                }

                if (player.getPlayer()?.uuid in throwers) {
                    checkPlayer(player)
                }
            }
        }
    }

    @SubscribeEvent
    fun onCommand(event: CommandRegistrationEvent) {
        event.register("dodge") {
            description = "dummy"
            callback { dodgeListCommand(it) }
        }

        event.register("ra-action-kick") {
            description = "dummy2"
            callback { kickPlayer(it.joinToString(" ")) }
        }
    }

    private fun dodgeListCommand(args: Array<String>) {
        if (false /* todo: config */) {
            ChatUtils.chat("§cThe dodge list feature is currently disabled. \n§7• §bEnable it in the SkyBlock category of §e/raven §bor §e/ra§b.")
            return
        }

        ravenAddons.launchCoroutine {
            val argument = args.getOrNull(0)
            val subcommand = subcommands.firstOrNull { it.name == argument }

            if (argument == null) {
                DodgeListHelp.execute(args.drop(1))
                return@launchCoroutine
            }
            if (subcommand == null) {
                DodgeListAdd.execute(args.drop(1))
                return@launchCoroutine
            }

            subcommand.execute(args.drop(1))
        }
    }

    private fun checkPlayer(player: String) { // todo: work on this
        ravenAddons.Companion.launchCoroutine {
            val playerUUID = player.getPlayer()?.uuid ?: return@launchCoroutine

            if (playerUUID !in throwers) {
                ChatUtils.debug("'$player' is not a thrower")
                return@launchCoroutine
            }

            val reason = throwers[playerUUID]?.reason ?: return@launchCoroutine
            val storedName = throwers[playerUUID]?.playerName ?: return@launchCoroutine

            val message = ChatComponentText("")


            if (storedName.lowercase() == player.lowercase()) {
                if (storedName != player) {

                    message.siblings.addAll(listOf(
                        DodgeListChatComponents.getLineComponent(),
                        prefixComponent,
                        ChatComponentText("§7§lUser Updated: §c$storedName §e→ §a$player\n"),
                        prefixComponent,
                        ChatComponentText("§7$player is on the dodge list! "),
                        removeComponent,
                        prefixComponent,
                        ChatComponentText("§7$player: §f$reason\n")
                    ))

                    TitleManager.setTitle("§c$storedName §e→ §a$player", "§e$reason", 1.5.seconds, 0.5.seconds, 0.5.seconds)
                    addPlayer(playerUUID, player, reason)
                } else {
                    message.siblings.addAll(listOf(
                        DodgeListChatComponents.getLineComponent(),
                        prefixComponent,
                        ChatComponentText("§7$player is on the dodge list! "),
                        removeComponent,
                        prefixComponent,
                        ChatComponentText("§7$player: §f$reason\n")
                    ))

                    TitleManager.setTitle("§e$player", "§e$reason", 1.5.seconds, 0.5.seconds, 0.5.seconds)
                }
            }
            message.siblings.add(announceComponent)
            message.siblings.add(kickComponent)
            message.siblings.add(blockComponent)
            message.siblings.add(DodgeListChatComponents.getLineComponent(false))

            ChatUtils.chat(message)
            SoundUtils.playSound("random.anvil_land", 1f, 1f)
        }
    }

    private fun kickPlayer(player: String) {
        ChatUtils.sendMessage("/pc [RA] Kicking $player since they are on the dodge list.")
        ravenAddons.Companion.launchCoroutine {
            Thread.sleep(500)

            ChatUtils.sendMessage("/p kick $player")
        }
    }

    fun addPlayer(uuid: UUID, name: String, reason: String) {
        throwers.put(uuid, DodgeListCustomData(name, reason))
        saveToFile()
    }

    fun removePlayer(uuid: UUID) {
        throwers.remove(uuid)
        saveToFile()
    }

    fun saveToFile() {
        ravenAddons.Companion.launchCoroutine {
            val gson = GsonBuilder().setPrettyPrinting().create()
            val jsonString = gson.toJson(throwers)

            configPath.mkdirs()
            if (!configFile.exists()) {
                configFile.createNewFile()
            }

            configFile.writeText(jsonString)
        }
    }

    private fun loadFromFile() {
        val gson = Gson()
        val type = object : TypeToken<Map<UUID, DodgeListCustomData>>() {}.type

        if (configFile.exists()) {
            try {
                val map: Map<UUID, DodgeListCustomData> = gson.fromJson(configFile.readText(), type)

                for ((uuid, reason) in map) {
                    throwers.put(uuid, reason)
                }
            } catch (_: Throwable) {
                //todo: remove this try catch before merging, this is just so nothing goes wrong from migration
            }
        }
    }
}

data class DodgeListCustomData(
    @Expose val playerName: String,
    @Expose val reason: String
)