package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.SkyBlockIsland
import at.raven.ravenAddons.data.commands.CommandCategory
import at.raven.ravenAddons.event.CommandRegistrationEvent
import at.raven.ravenAddons.event.ConfigFixEvent
import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenAddons.event.render.WorldRenderEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.ClipboardUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.StringUtils.removeColors
import at.raven.ravenAddons.utils.TitleManager
import at.raven.ravenAddons.utils.render.WorldRenderUtils.drawString
import kotlinx.coroutines.delay
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.Vec3
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import kotlin.time.Duration.Companion.seconds

@LoadModule
object FireFreezeTimer {
    private val frozenEntities: MutableMap<EntityLivingBase, SimpleTimeMark> = mutableMapOf()
    private var freezeMessageCooldown = SimpleTimeMark.farPast()
    private var unfreezeMessageCooldown = SimpleTimeMark.farPast()
    private var titleCooldown = SimpleTimeMark.farPast()

    // https://regex101.com/r/YwLEWt/4
    private val armorStandPattern =
        "^(?:﴾ )?(?:\\[Lv\\d+] )?(?:[✈☮⚓♃Ж⚙⚂♣⊙☃❄✰♨♆✿ൠ⛨\uD83E\uDDB4☽⛏༕☠⸙]+ )?(?<name>[\\w ]+) [\\d.,\\/kMB]+❤(?: [﴿✯])?\$".toPattern()

    @SubscribeEvent
    fun onPlaySound(event: PlaySoundEvent) {
        if (!HypixelGame.inSkyBlock || SkyBlockIsland.CATACOMBS.isInIsland()) return
        if (event.name != "random.anvil_land") return
        if (event.sound.pitch != 0.4920635f) return
        if (event.sound.volume != 0.6f) return

        var entityName: String? = null

        val location =
            Vec3(event.sound.xPosF.toDouble(), event.sound.yPosF.toDouble(), event.sound.zPosF.toDouble())

        val entities = checkNearbyEntities(location).filter { it !in frozenEntities }
        if (entities.isEmpty()) return

        frozenEntities.putAll(entities.associateWith { SimpleTimeMark.now() + 10.seconds })

        val entityCount = entities.size
        val firstEntity = entities.firstOrNull()
        if (entities.size == 1 && firstEntity != null) {
            entityName = firstEntity.getMatchedName() ?: firstEntity.name
        }
        ChatUtils.debug("Fire Freeze Timer: $entityName.")

        if (ravenAddons.config.fireFreezeNotification && titleCooldown.isInPast()) {
            titleCooldown = SimpleTimeMark.now() + 1.seconds
            ravenAddons.runDelayed(5.seconds) {
                TitleManager.setTitle("§b§lFREEZE!", "", 3.seconds, 1.seconds, 1.seconds)
                ChatUtils.chat("Fire Freeze Staff is ready for re-use.")
            }
        }

        if (ravenAddons.config.fireFreezeAnnounce == 1 || ravenAddons.config.fireFreezeAnnounce == 3) {
            ChatUtils.debug("Fire Freeze Announce: Sending message in 250ms.")
            if (freezeMessageCooldown.isInPast()) {
                freezeMessageCooldown = SimpleTimeMark.now() + 5.seconds
                ravenAddons.launchCoroutine {
                    delay(250)
                    if (entityName == null) {
                        val string = if (entityCount == 1) "Mob" else "$entityCount Mobs"

                        ChatUtils.sendMessage("/pc [RA] $string frozen!")
                    }
                    else
                        ChatUtils.sendMessage("/pc [RA] $entityName frozen!")
                }
            }
        }
    }

    @SubscribeEvent
    fun onWorldRender(event: WorldRenderEvent) {
        if (!HypixelGame.inSkyBlock || SkyBlockIsland.CATACOMBS.isInIsland() || !ravenAddons.config.fireFreezeTimer) return

        val entities = frozenEntities.toMap()
        for ((entity, timer) in entities) {
            if (timer.isInPast() || !entity.isInWorld()) {
                frozenEntities.remove(entity)
                ChatUtils.debug("Fire Freeze Announce: Frozen entity died or is now unfrozen.")
                if (ravenAddons.config.fireFreezeAnnounce == 2 || ravenAddons.config.fireFreezeAnnounce == 3 && unfreezeMessageCooldown.isInPast() && entity.isInWorld()) {
                    ChatUtils.debug("Fire Freeze Announce: Sending message for unfrozen mob.")
                    ChatUtils.sendMessage("/pc [RA] Mob(s) unfroze!")
                    unfreezeMessageCooldown = SimpleTimeMark.now() + 5.seconds
                }
                continue
            }

            val duration = "%.2f".format(timer.timeUntil().inWholeMilliseconds / 1000.0)
            event.drawString(null, entity, "§b❄ $duration", false, Color.WHITE, event.partialTicks, Vec3(0.0, 0.90, 0.0))
        }
    }

    private fun checkNearbyEntities(location: Vec3): List<EntityLivingBase> {
        val entities = ravenAddons.mc.theWorld.loadedEntityList ?: return emptyList()
        val entitiesToFreeze = mutableListOf<EntityLivingBase>()
        for (entity in entities) {
            if (entity !is EntityLivingBase) continue

            if (entity is EntityPlayerSP) continue
            if (entity is EntityPlayer && entity.uniqueID.version() == 4) continue
            if (entity is EntityArmorStand) continue
            if (entity.positionVector.distanceTo(location) > 5) continue

            if (entity in frozenEntities) continue
            entitiesToFreeze.add(entity)
        }

        return entitiesToFreeze
    }

    @SubscribeEvent
    fun onHypixelServerChange(event: HypixelServerChangeEvent) {
        frozenEntities.clear()
    }

    private fun Entity.isInWorld(): Boolean {
        val entityList = ravenAddons.mc.theWorld.loadedEntityList ?: return false

        return this in entityList
    }

    private fun Entity.getMatchedName(fromCommand: Boolean = false): String? {
        val name = this.getArmorStandName()

        armorStandPattern.matchMatcher(name?.removeColors() ?: "") {
            val group = group("name")

            return group
        }
        if (!fromCommand) {
            ChatUtils.chatClickable(
                message = "§8[§cRA ERROR§8] §7Unknown mob detected! Click here to run §f/ra copyentities§7.",
                command = "/ra copyentities",
                usePrefix = false
            )
            println("'${name?.removeColors()}'")
        }
        return null
    }

    private fun Entity.getArmorStandName(): String? {
        val worldEntities = ravenAddons.mc.theWorld.loadedEntityList ?: return null
        val armorStandList = worldEntities.filter { it is EntityArmorStand && it.customNameTag.contains("❤") }
        if (armorStandList.isEmpty()) return null
        val armorStand = armorStandList.minBy { it.positionVector.distanceTo(this.positionVector) } ?: return null

        return armorStand.customNameTag
    }

    @SubscribeEvent
    fun onCommandRegistration(event: CommandRegistrationEvent) {
        event.register("copyentities") {
            description = "Copy nearby entities for proper name detection."
            category = CommandCategory.DEVELOPER
            callback { copyEntitiesCommand() }
        }
    }

    private fun copyEntitiesCommand() {
        val worldEntities = ravenAddons.mc.theWorld.loadedEntityList ?: return
        val mobList = worldEntities.filter {
            it is EntityLivingBase && it !is EntityPlayerSP && it !is EntityArmorStand
        }

        val matchedMobs = mutableListOf<Pair<String, String>>()  //armor stand name, mob name
        val unmatchedMobs = mutableListOf<Pair<String, String>>()  //armor stand name, vanilla name

        loop@ for (mob in mobList) {
            val mobCustomName = mob.getArmorStandName()?.removeColors() ?: continue

            armorStandPattern.matchMatcher(mobCustomName) {
                matchedMobs.add(mobCustomName to group("name"))
                continue@loop
            }

            unmatchedMobs.add(mobCustomName to mob.name)
        }

        var stringToCopy = "------------------\n"
        stringToCopy += "matched ${matchedMobs.size} mobs\n"
        matchedMobs.forEach {
            stringToCopy += it.second + " | '" + it.first + "'\n"
        }
        stringToCopy += "---------\n"
        stringToCopy += "couldn't match ${unmatchedMobs.size} mobs\n"
        unmatchedMobs.forEach {
            stringToCopy += it.second + " | '" + it.first + "'\n"
        }
        stringToCopy += "------------------\n"

        ClipboardUtils.copyToClipboard(stringToCopy)
        ChatUtils.chat("Copied ${matchedMobs.size + unmatchedMobs.size} mobs to the clipboard")
    }

    @SubscribeEvent
    fun onConfigFix(event: ConfigFixEvent) {
//         event.checkVersion(150) {
//             val tomlData = event.tomlData ?: return@checkVersion
//             val announcerValue = tomlData.get<Boolean>("skyblock.fire_freeze_staff.fire_freeze_announcer")
//
//             val newValue = if (announcerValue) 3 else 0
//
//             tomlData.remove<Boolean>("skyblock.fire_freeze_staff.fire_freeze_announcer")
//             tomlData.set<Int>("skyblock.fire_freeze_staff.fire_freeze_announcer")
//             event.tomlData = tomlData
//         }
    }
}
