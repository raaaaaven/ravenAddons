package at.raven.ravenAddons.event

import at.raven.ravenAddons.event.base.RavenEvent
import at.raven.ravenAddons.utils.PlayerUtils
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3

class EntityTeleportEvent(val newPosition: Vec3, val entity: Entity) : RavenEvent() {
    val distanceToPlayer: Double? get() {
        val playerPosition = PlayerUtils.getPlayer()?.positionVector ?: return null
        return newPosition.distanceTo(playerPosition)
    }
}