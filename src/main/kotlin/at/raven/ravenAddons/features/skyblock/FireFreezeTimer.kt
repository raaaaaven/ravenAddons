package at.raven.ravenAddons.features.skyblock

import at.raven.ravenAddons.config.ravenAddonsConfig
import at.raven.ravenAddons.data.HypixelGame
import at.raven.ravenAddons.data.HypixelGame.Companion.isNotPlaying
import at.raven.ravenAddons.event.hypixel.HypixelServerChangeEvent
import at.raven.ravenAddons.event.render.WorldRenderEvent
import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.ChatUtils
import at.raven.ravenAddons.utils.SimpleTimeMark
import at.raven.ravenAddons.utils.render.WorldRenderUtils.drawString
import net.minecraft.client.entity.EntityPlayerSP
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

    @SubscribeEvent
    fun onPlaySound(event: PlaySoundEvent) {
        if (ravenAddonsConfig.fireFreezeTimer) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return
        if (event.name != "random.anvil_land") return
        if (event.sound.pitch != 0.4920635f) return
        if (event.sound.volume != 0.6f) return

        val location = Vec3(event.sound.xPosF.toDouble(), event.sound.yPosF.toDouble(), event.sound.zPosF.toDouble())

        val entities = checkNearbyEntities(location).associate { it to SimpleTimeMark.now() + 10.seconds }

        frozenEntities.clear()
        frozenEntities.putAll(entities)
        }

        if (ravenAddonsConfig.fireFreezeAnnounce) {
            ChatUtils.debug("fireFreezeAnnounce: sending message")

            ChatUtils.sendMessage("/pc [RA] Mob(s) frozen!")
        }
    }

    @SubscribeEvent
    fun onWorldRender(event: WorldRenderEvent) {
        if (HypixelGame.SKYBLOCK.isNotPlaying()) return

        val entities = frozenEntities.toMap()
        for ((entity, timer) in entities) {
            if (timer.isInPast() || entity !in (ravenAddons.mc.theWorld.loadedEntityList ?: emptyList())) {
                frozenEntities.remove(entity)
                ChatUtils.sendMessage("/pc [RA] Mob(s) died or is now unfrozen!")
                continue
            }

            val duration = "%.2f".format(timer.timeUntil().inWholeMilliseconds/1000.0)
            event.drawString(null, entity, "§b❄ $duration", false, Color.WHITE, event.partialTicks, Vec3(0.0, 0.5, 0.0))
        }
    }

    private fun checkNearbyEntities(location: Vec3): List<EntityLivingBase> {
        val entities = ravenAddons.mc.theWorld.loadedEntityList ?: return emptyList()
        val frozenEntities = mutableListOf<EntityLivingBase>()
        for (entity in entities) {
            if (entity !is EntityLivingBase) continue

            if (entity is EntityPlayerSP) continue
            if (entity is EntityPlayer && entity.uniqueID.version() == 4) continue
            if (entity is EntityArmorStand) continue
            if (entity.positionVector.distanceTo(location) > 5) continue

            frozenEntities.add(entity)
        }

        return frozenEntities.toList()
    }

    @SubscribeEvent
    fun onHypixelServerChange(event: HypixelServerChangeEvent) {
        frozenEntities.clear()
    }
}