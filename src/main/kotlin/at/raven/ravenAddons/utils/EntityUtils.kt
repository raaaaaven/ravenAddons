package at.raven.ravenAddons.utils

import at.raven.ravenAddons.ravenAddons
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer

object EntityUtils {
    fun getEntityByID(id: Int): Entity? {
        val theWorld = ravenAddons.mc.theWorld ?: return null
        return theWorld.getEntityByID(id)
    }

    fun EntityPlayer.isRealPlayer() = this.uniqueID?.let { it.version() == 4 } == true
}