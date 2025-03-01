package at.raven.ravenAddons.features.skyblock.dodgelist

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.PartyAPI
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.PartyUpdateEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents.getAnnounceComponent
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents.getBlockComponent
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents.getKickComponent
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents.getRemoveComponent
import at.raven.ravenAddons.features.skyblock.dodgelist.DodgeListChatComponents.prefixComponent
import at.raven.ravenAddons.features.skyblock.dodgelist.subcommands.DodgeListAdd
import at.raven.ravenAddons.features.skyblock.dodgelist.subcommands.DodgeListHelp
import at.raven.ravenAddons.features.skyblock.dodgelist.subcommands.DodgeListList
import at.raven.ravenAddons.features.skyblock.dodgelist.subcommands.DodgeListRemove
import at.raven.ravenAddons.features.skyblock.dodgelist.subcommands.DodgeListReset
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ChatUtils.add
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.PlayerUtils.getPlayer
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.SoundUtils
import at.raven.ravenAddons.utils.StringUtils.removeColors
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

    internal val throwers: MutableMap<UUID, DodgeListCustomData> = mutableMapOf()

    private var partyListCheck = SimpleTimeMark.farPast()

    init {
        loadFromFile()
    }

    val subcommands = listOf<DodgeListSubcommand>( //having this be automatic might be cool
        DodgeListAdd,
        DodgeListRemove,
        DodgeListList,
        DodgeListReset,
        DodgeListHelp,
    )

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        if (!ravenAddonsConfig.dodgeList) return

        if (fullPartyPattern.matches(event.message.removeColors())) {
            if (!ravenAddonsConfig.dodgeListFullPartyCheck) {
                ChatUtils.debug("dodgeListFullPartyCheck: checking")
                ChatUtils.chat("Checking for people in the dodge list.")
                PartyAPI.sendPartyPacket()
                partyListCheck = SimpleTimeMark.now()
            }
        }
        ravenAddons.launchCoroutine {
            playerJoinPattern.matchMatcher(event.message.removeColors()) {
                val playerName = group("name")

                if (playerName == PlayerUtils.playerName) {
                    PartyAPI.sendPartyPacket()
                    partyListCheck = SimpleTimeMark.now()
                    return@launchCoroutine
                }

                val player = playerName.getPlayer() ?: run {
                    ChatUtils.warning("Couldn't fetch player $playerName! (nicked?)")
                    return@launchCoroutine
                }
                if (player.uuid in throwers) {
                    checkPlayer(player, playerName)
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
        if (!ravenAddonsConfig.dodgeList) {
            ChatUtils.chat("§cThe dodge list feature is currently disabled. \n§7• §bEnable it in the SkyBlock category of §e/raven §bor §e/ra§b.")
            return
        }

        ravenAddons.launchCoroutine {
            val argument = args.getOrNull(0)
            val subcommand = subcommands.firstOrNull { it.name == argument || argument in it.aliases}

            if (argument == null) {
                DodgeListHelp.execute(args.drop(1))
                return@launchCoroutine
            }
            if (subcommand == null) {
                DodgeListAdd.execute(args.toList())
                return@launchCoroutine
            }

            subcommand.execute(args.drop(1))
        }
    }

    private fun checkPlayer(player: PlayerUtils.PlayerIdentifier, newPlayerName: String) {
        val data = throwers.getOrElse(player.uuid) {
            ChatUtils.debug("$newPlayerName uuid in list but no data somehow, weird!")
            return
        }

        val message = ChatComponentText("")
        message.add(DodgeListChatComponents.getLineComponent())

        if (data.playerName != newPlayerName) {
            message.add(prefixComponent)
            message.add("§7§lUser Updated: §c${data.playerName} §e→ §a$newPlayerName\n")
            TitleManager.setTitle("§c${data.playerName} §e→ §a$newPlayerName", "§e${data.actualReason}", 1.5.seconds, 0.5.seconds, 0.5.seconds)
            addPlayer(player.uuid, newPlayerName, data.reason)
        } else {
            TitleManager.setTitle("§e$newPlayerName", "§e${data.actualReason}", 1.5.seconds, 0.5.seconds, 0.5.seconds)
        }

        message.add(prefixComponent)
        message.add("§7$newPlayerName is on the dodge list! ")
        message.add(getRemoveComponent(newPlayerName))
        message.add(prefixComponent)
        message.add("§7$newPlayerName: §f${data.actualReason}\n")
        message.add(getAnnounceComponent(newPlayerName))
        message.add(getKickComponent(newPlayerName))
        message.add(getBlockComponent(newPlayerName))
        message.add(DodgeListChatComponents.getLineComponent(false))

        ChatUtils.chat(message)
        SoundUtils.playSound("random.anvil_land", 1f, 1f)
    }

    @SubscribeEvent
    fun onPartyUpdate(event: PartyUpdateEvent) {
        if (!ravenAddonsConfig.dodgeList) return
        if (partyListCheck.passedSince() > 10.seconds) return

        ravenAddons.launchCoroutine {
            for ((player, _) in event.partyList) {
                checkPlayer(player, player.name)
            }
        }
    }

    private fun kickPlayer(player: String) {
        ChatUtils.sendMessage("/pc [RA] Kicking $player since they are on the dodge list.")
        ravenAddons.Companion.launchCoroutine {
            Thread.sleep(500)

            ChatUtils.sendMessage("/p kick $player")
        }
    }

    fun addPlayer(uuid: UUID, data: DodgeListCustomData) {
        throwers.put(uuid, data)
        saveToFile()
    }

    fun addPlayer(uuid: UUID, name: String, reason: String?) {
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

        if (configFile.exists()) {
            try {
                val type = object : TypeToken<Map<UUID, DodgeListCustomData>>() {}.type
                val map: Map<UUID, DodgeListCustomData> = gson.fromJson(configFile.readText(), type)

                for ((uuid, reason) in map) {
                    throwers.put(uuid, reason)
                }
            } catch (_: Throwable) {
                val type = object : TypeToken<Map<UUID, Pair<String, String>>>() {}.type
                val map: Map<UUID, Pair<String, String>> = gson.fromJson(configFile.readText(), type)

                for ((uuid, reason) in map) {
                    throwers.put(uuid, DodgeListCustomData(reason.first, reason.second))
                }
            }
        }
    }
}

data class DodgeListCustomData(
    @Expose val playerName: String,
    @Expose val reason: String?
) {
    val actualReason: String get() = reason ?: "No reason provided!"
}