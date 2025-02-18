package at.raven.ravenaddons.utils

import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP

object PlayerUtils {
    val playerName: String get() = Minecraft.getMinecraft().thePlayer.name

    fun getPlayer(): EntityPlayerSP? = Minecraft.getMinecraft().thePlayer
}