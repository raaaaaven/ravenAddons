package at.raven.ravenAddons.features.dungeons

import at.raven.ravenAddons.data.PartyAPI
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.chat.ChatReceivedEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.PlayerUtils
import at.raven.ravenAddons.utils.PlayerUtils.getPlayerName
import at.raven.ravenAddons.utils.PlayerUtils.getPlayerUUID
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
import at.raven.ravenAddons.utils.SoundUtils
import at.raven.ravenAddons.utils.StringUtils.cleanupColors
import at.raven.ravenAddons.utils.TitleManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.File
import java.lang.Thread.sleep
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

@LoadModule
object DodgeList {
    private val fullPartyPattern =
        "Party Finder > Your dungeon group is full! Click here to warp to the dungeon!".toPattern()
    private val playerJoinPattern =
        "Party Finder > (?<name>.+) joined the dungeon group! .+".toPattern()

    private val configPath = File("config/ravenAddons")
    private val configFile = File(configPath, "dodgeList.json")

    // uuid + (name, reason)
    private val throwers: MutableMap<UUID, Pair<String, String>> = mutableMapOf()

    init {
        loadFromFile()
    }

    @SubscribeEvent
    fun onChat(event: ChatReceivedEvent) {
        //config here
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

                if (player.getPlayerUUID() in throwers) {
                    checkPlayer(player.lowercase())
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
        if (false /*config*/) {
            ChatUtils.chat("§cThe dodge list feature is currently disabled. \\n§7• §bEnable it in the SkyBlock category of §e/raven §bor §e/ra§b.")
            return
        }

        if (args.isEmpty()) {
            var prefixComponent = ChatComponentText(
                "§8§m-----------------------------------------------------\n" +
                        "§7• §b/dodge <player> <reason> §8- §7Add a player to the dodge list with a reason.\n" +
                        "§7• §b/dodge remove <player> §8- §7Remove a player from the dodge list."
            )

            var listComponent = ChatComponentText("§7• §b/dodge list §8 - §7List everyone on the dodge list.")

            listComponent.chatStyle.chatHoverEvent =
                HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§bClick here to run §e/dodge list§b."))
            listComponent.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dodge list")

            var resetComponent = ChatComponentText("§7• §b/dodge reset §8- §7Remove all players from the dodge list.")

            resetComponent.chatStyle.chatHoverEvent =
                HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§bClick here to run §e/dodge reset§b."))
            resetComponent.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dodge reset")

            var suffixComponent = ChatComponentText("§8§m-----------------------------------------------------")

            ChatUtils.chat(prefixComponent)
            ChatUtils.chat(listComponent)
            ChatUtils.chat(resetComponent)
            ChatUtils.chat(suffixComponent)

            return
        }

        val argument = args.getOrNull(0) ?: return

        when (argument) {
            "remove" -> removePlayer(args.drop(1).toList())
            "list" -> listPlayers()
            "reset" -> resetList()
            else -> addPlayer(args.toList())
        }
    }

    private fun removePlayer(args: List<String>) {
        ravenAddons.launchCoroutine {
            val player = args.firstOrNull() ?: return@launchCoroutine
            val playerUUID = player.getPlayerUUID()

            val message: String = if (playerUUID in throwers) {
                "§8§m-----------------------------------------------------\n" +
                        "§7Removed §c$player §7from the list.\n" +
                        "§8§m-----------------------------------------------------\n"
            } else {
                "§8§m-----------------------------------------------------\n" +
                        "§7Player §c$player §7not found in the list." +
                        "§8§m-----------------------------------------------------\n"
            }
            ChatUtils.chat(message, usePrefix = false)

            playerUUID?.let { removePlayer(it) }
        }
    }

    private fun listPlayers() {
        ravenAddons.launchCoroutine {
            var message = "§8§m-----------------------------------------------------\n"
            val changedNamesMap = mutableMapOf<String, String>() //old name -> new name

            if (throwers.isNotEmpty()) {
                for ((player, reason) in throwers) {
                    val newPlayerName = player.getPlayerName() ?: continue
                    if (newPlayerName != reason.first) {
                        changedNamesMap.put(reason.first, newPlayerName)
                    }
                    addPlayer(player, newPlayerName, reason.second)

                    message += "§7• §b${newPlayerName}§7: §f${reason.second}\n"
                }
            } else {
                message += " §7The dodge list is currently empty!\n" //TODO: better text
            }

            message += "§8§m-----------------------------------------------------"

            ChatUtils.chat(message, usePrefix = false)

            //TODO: display changedNamesMap
        }
    }

    private var resetConfirmation = false

    private fun resetList() {
        if (!resetConfirmation) {
            resetConfirmation = true

            ChatUtils.chat("§cType §e/dodge reset §cagain to confirm.")
            ravenAddons.launchCoroutine {
                sleep(5000)
                if (resetConfirmation == false) return@launchCoroutine
                resetConfirmation = false
                ChatUtils.chat("§cThe confirmation period expired.")
            }

            return
        }

        resetConfirmation = false
        throwers.clear()
        ChatUtils.chat("§bSuccessfully reset the list.")
    }

    private fun addPlayer(args: List<String>) {
        ravenAddons.launchCoroutine {
            val player = args.firstOrNull() ?: return@launchCoroutine
            val playerUUID = player.getPlayerUUID() ?: run {
                ChatUtils.warning("couldn't fetch uuid for $player")
                return@launchCoroutine
            }
            var reason = args.drop(1).joinToString(" ")

            if (reason.isEmpty()) {
                reason = "No reason provided."
            }

            addPlayer(playerUUID, player, reason)

            val message = "§8§m-----------------------------------------------------\n" +
                    "§7Added §a$player§7 to the list.\n" +
                    "§f$reason\n" +
                    "§8§m-----------------------------------------------------\n"

            ChatUtils.chat(message, usePrefix = false)
        }
    }

    private fun checkPlayer(player: String) {
        ravenAddons.launchCoroutine {
            val playerUUID = player.getPlayerUUID() ?: return@launchCoroutine

            if (playerUUID !in throwers) {
                ChatUtils.debug("'$player' is not a thrower")
                return@launchCoroutine
            }

            val reason = throwers[playerUUID]?.second ?: return@launchCoroutine
            val storedName = throwers[playerUUID]?.first ?: return@launchCoroutine

            val message = ChatComponentText("")
            val lineComponent = ChatComponentText("§8§m-----------------------------------------------------\n")
            val prefixComponent = ChatComponentText("§8[§cRA§8] ")
            message.siblings.add(prefixComponent)

            val announceComponent = ChatComponentText("§9§l[ANNOUNCE] ")
            announceComponent.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pc [RA] $player is on the dodge list!")
            announceComponent.chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to announce to party chat."))
            message.siblings.add(announceComponent)

            val kickComponent = ChatComponentText("§c§l[KICK] ")
            kickComponent.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ra-action-kick $player")
            kickComponent.chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to kick the dodged user."))
            message.siblings.add(kickComponent)

            val blockComponent = ChatComponentText("§7§l[BLOCK] ")
            blockComponent.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/block add $player")
            blockComponent.chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to block the dodged user."))
            message.siblings.add(blockComponent)

            val removeComponent = ChatComponentText("§c§l[REMOVE]\n")
            removeComponent.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/dodge remove $player")
            removeComponent.chatStyle.chatHoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ChatComponentText("§7Click here to remove the user from the dodge list."))

            message.siblings.add(ChatComponentText("§8§m-----------------------------------------------------"))

            if (storedName.lowercase() == player.lowercase()) {
                if (storedName != player) {
                    val newMessage = ChatComponentText("")
                    newMessage.siblings.add(lineComponent)
                    newMessage.siblings.add(
                        ChatComponentText("§7§lUser Updated: §c$storedName §e→ §a$player\n")
                    )

                    val chatComponent = ChatComponentText("")
                    chatComponent.siblings.addAll(listOf(
                        prefixComponent,
                        ChatComponentText("§7$player is on the dodge list!"),
                        removeComponent,
                        ChatComponentText("§7$player: §f$reason")
                    ))

                    ChatUtils.chat(chatComponent)
                    TitleManager.setTitle("§c$storedName §e→ §a$player", "§e$reason", 1.5.seconds, 0.5.seconds, 0.5.seconds)
                    addPlayer(playerUUID, player, reason)
                } else {
                    val chatComponent = ChatComponentText("")
                    chatComponent.siblings.addAll(listOf(
                        lineComponent,
                        prefixComponent,
                        ChatComponentText("§7$player is on the dodge list! "),
                        removeComponent,
                        prefixComponent,
                        ChatComponentText("§7$player: §f$reason")
                    ))

                    ChatUtils.chat(chatComponent)
                    TitleManager.setTitle("§e$player", "§e$reason", 1.5.seconds, 0.5.seconds, 0.5.seconds)
                }
            }

            ChatUtils.chat(message)
            SoundUtils.playSound("random.anvil_land", 1f, 1f)
        }
    }

    private fun kickPlayer(player: String) {
        ChatUtils.sendMessage("/pc [RA] Kicking $player since they are on the dodge list.")
        ravenAddons.launchCoroutine {
            sleep(500)

            ChatUtils.sendMessage("/p kick $player")
        }
    }

    private fun addPlayer(uuid: UUID, name: String, reason: String) {
        throwers.put(uuid, name to reason)
        saveToFile()
    }

    private fun removePlayer(uuid: UUID) {
        throwers.remove(uuid)
        saveToFile()
    }

    private fun saveToFile() {
        ravenAddons.launchCoroutine {
            val gson = GsonBuilder().create()
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
        val type = object : TypeToken<Map<UUID, Pair<String, String>>>() {}.type

        if (configFile.exists()) {
            val map: Map<UUID, Pair<String, String>> = gson.fromJson(configFile.readText(), type)

            for ((uuid, reason) in map) {
                throwers.put(uuid, reason)
            }
        }
    }
}