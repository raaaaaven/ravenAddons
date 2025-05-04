package at.raven.ravenAddons.event

import at.raven.ravenAddons.utils.PlayerUtils
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3
import net.minecraftforge.fml.common.eventhandler.Event

class EntityTeleportEvent(val newPosition: Vec3, val entity: Entity) : Event() {
    val distanceToPlayer: Double? get() {
        val playerPosition = PlayerUtils.getPlayer()?.positionVector ?: return null
        return newPosition.distanceTo(playerPosition)
    }
}