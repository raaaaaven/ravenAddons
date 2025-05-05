package at.raven.ravenAddons.utils

import at.raven.ravenAddons.ravenAddons
import at.raven.ravenAddons.utils.APIUtils.getJsonObjectResponse
import com.mojang.authlib.GameProfile
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import java.net.URL
import java.util.*

object PlayerUtils {
    private var uuidPlayerCache: Map<UUID, String> = emptyMap()

    val playerName: String get() = Minecraft.getMinecraft().thePlayer.name

    fun getPlayer(): EntityPlayerSP? = Minecraft.getMinecraft().thePlayer

    private fun String.getGameProfileFromWorld(): GameProfile? = ravenAddons.mc?.netHandler?.getPlayerInfo(this)?.gameProfile
    private fun UUID.getGameProfileFromWorld(): GameProfile? = ravenAddons.mc?.netHandler?.getPlayerInfo(this)?.gameProfile

    private suspend fun String.getFromMojang(): PlayerIdentifier? {
        return try {
            val json = URL("https://api.mojang.com/users/profiles/minecraft/$this").getJsonObjectResponse()?.asJsonObject ?: return null

            val uuidString = json["id"].asString
            val name = json["name"].asString.removeSuffix("\n")

            PlayerIdentifier(
                name,
                UUID.fromString(
                    uuidString.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                        "$1-$2-$3-$4-$5")
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ChatUtils.debug("something exploded!!")
            null
        }
    }

    private suspend fun UUID.getNameFromMojang(): PlayerIdentifier? {
        return try {
            val json = URL("https://api.mojang.com/user/profile/$this").getJsonObjectResponse() ?: return null

            val uuidString = json["id"].asString
            val name = json["name"].asString

            PlayerIdentifier(
                name,
                UUID.fromString(
                    uuidString.replaceFirst(
                        "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                        "$1-$2-$3-$4-$5")
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ChatUtils.debug("something exploded!!")
            null
        }
    }

    suspend fun String.getPlayer(): PlayerIdentifier? {
        // check if the value is cached
        uuidPlayerCache.entries.firstOrNull { it.value == this }?.let { return PlayerIdentifier(it.value, it.key) }

        // if not, check if the player is in the world
        this.getGameProfileFromWorld()?.let { return PlayerIdentifier(it.name, it.id).addToMapAndReturn() }

        // if not, check the mojang api
        return this.getFromMojang()?.addToMapAndReturn()
    }

    suspend fun UUID.getPlayer(): PlayerIdentifier? {
        // check if the value is cached
        uuidPlayerCache[this]?.let { return PlayerIdentifier(it, this) }

        // if not, check if the player is in the world
        this.getGameProfileFromWorld()?.let { return PlayerIdentifier(it.name, it.id).addToMapAndReturn() }

        // if not, check the mojang api
        return this.getNameFromMojang()?.addToMapAndReturn()
    }

    private fun PlayerIdentifier.addToMapAndReturn(): PlayerIdentifier {
        val newMap = uuidPlayerCache.toMutableMap()
        newMap.put(this.uuid, this.name)
        uuidPlayerCache = newMap.toMap()
        return this
    }

    data class PlayerIdentifier(
        val name: String,
        val uuid: UUID
    )
}