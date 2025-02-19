package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenAddons.event.render.WorldRenderEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.RegexUtils.matchMatcher
import at.raven.ravenAddons.utils.RegexUtils.matches
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
    private var messageCooldown = SimpleTimeMark.farPast()

    private val armorStandPattern = "^(\\[Lv\\d+] )?(?<name>[\\w ]+) [\\d.,/kmb]+❤".toPattern()

    @SubscribeEvent
    fun onPlaySound(event: PlaySoundEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (event.name != "random.anvil_land") return
        if (event.sound.pitch != 0.4920635f) return
        if (event.sound.volume != 0.6f) return

        var entityName: String? = null
        var entityCount: Int? = null

        val location =
            Vec3(event.sound.xPosF.toDouble(), event.sound.yPosF.toDouble(), event.sound.zPosF.toDouble())

        val entities = checkNearbyEntities(location)

        frozenEntities.clear()
        frozenEntities.putAll(entities.associate { it to SimpleTimeMark.now() + 10.seconds })

        entityCount = entities.size
        val firstEntity = entities.toList().firstOrNull()
        if (entities.size == 1 && firstEntity != null) {
            entityName = firstEntity.getNearestArmorStand() ?: firstEntity.name
        }
        ChatUtils.debug(entityName.toString())

        if (ravenAddonsConfig.fireFreezeNotification) {
            ravenAddons.launchCoroutine { //TODO: triggers once per entity plz fix
                delay(5000)

                TitleManager.setTitle("", "§bRE-FREEZE!", 1.seconds, 0.5.seconds, 0.5.seconds)
                ChatUtils.chat("Fire Freeze Staff is ready for re-freezing.")
            }
        }

        if (ravenAddonsConfig.fireFreezeAnnounce) {
            if (messageCooldown.isInPast()) {
                ChatUtils.debug("fireFreezeAnnounce: sending message")
                if (entityName == null) {
                    val string = if (entityCount == 1) "Mob" else "$entityCount Mobs"

                    ChatUtils.sendMessage("/pc [RA] $string frozen!")
                }
                else
                    ChatUtils.sendMessage("/pc [RA] $entityName frozen!")
                messageCooldown = SimpleTimeMark.now() + 5.seconds
            }
        }
    }

    @SubscribeEvent
    fun onWorldRender(event: WorldRenderEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (!ravenAddonsConfig.fireFreezeTimer) return

        val entities = frozenEntities.toMap()
        for ((entity, timer) in entities) {
            if (timer.isInPast() || !entity.isInWorld()) {
                frozenEntities.remove(entity)
                if (messageCooldown.isInPast() && entity.isInWorld()) {
                    ChatUtils.debug("fireFreezeAnnounce: frozen entity died or is now unfrozen!")
                    ChatUtils.sendMessage("/pc [RA] Mob(s) unfroze!")
                    messageCooldown = SimpleTimeMark.now() + 5.seconds
                }
                continue
            }

            val duration = "%.2f".format(timer.timeUntil().inWholeMilliseconds / 1000.0)
            event.drawString(null, entity, "§b❄ $duration", false, Color.WHITE, event.partialTicks, Vec3(0.0, 0.5, 0.0))
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

        return entitiesToFreeze.toList()
    }

    @SubscribeEvent
    fun onHypixelServerChange(event: HypixelServerChangeEvent) {
        frozenEntities.clear()
    }

    private fun Entity.isInWorld(): Boolean {
        val entityList = ravenAddons.mc.theWorld.loadedEntityList ?: return false

        return this in entityList
    }

    private fun Entity.getNearestArmorStand(): String? {
        val worldEntities = ravenAddons.mc.theWorld.loadedEntityList ?: return null
        val armorStandList = worldEntities.filter { it is EntityArmorStand && armorStandPattern.matches(it.customNameTag.removeColors()) }
        if (armorStandList.isEmpty()) return null
        val armorStand = armorStandList.minBy { it.positionVector.distanceTo(this.positionVector) } ?: return null

        armorStandPattern.matchMatcher(armorStand.customNameTag.removeColors()) {
            return group("name")
        }
        return null
    }
}