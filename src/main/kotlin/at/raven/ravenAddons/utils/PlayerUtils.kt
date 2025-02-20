package at.raven.ravenAddons.utils

import at.raven.ravenAddons.ravenAddons
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.mojang.authlib.GameProfile
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

object PlayerUtils {
    private var uuidPlayerCache: Map<UUID, String> = emptyMap()

    val playerName: String get() = Minecraft.getMinecraft().thePlayer.name

    fun getPlayer(): EntityPlayerSP? = Minecraft.getMinecraft().thePlayer

    private fun String.getGameProfileFromWorld(): GameProfile? = ravenAddons.mc?.netHandler?.getPlayerInfo(this)?.gameProfile
    private fun UUID.getGameProfileFromWorld(): GameProfile? = ravenAddons.mc?.netHandler?.getPlayerInfo(this)?.gameProfile

    private suspend fun String.getUUIDFromMojang(): UUID? {
        return try {
            val connection = URL("https://api.mojang.com/users/profiles/minecraft/$this").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val response = connection.inputStream.bufferedReader().use { it.readText() }

            val uuid = Gson().fromJson(response, JsonObject::class.java).get("id").asString

            UUID.fromString(
                uuid.replaceFirst(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                    "$1-$2-$3-$4-$5")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            ChatUtils.warning("something exploded!!")
            null
        }
    }

    private suspend fun UUID.getNameFromMojang(): String? {
        return try {
            val connection = URL("https://api.mojang.com/user/profile/$this").openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connect()

            val response = connection.inputStream.bufferedReader().use { it.readText() }

            Gson().fromJson(response, JsonObject::class.java).get("name").asString
        } catch (e: Exception) {
            e.printStackTrace()
            ChatUtils.warning("something exploded!!")
            null
        }
    }

    suspend fun UUID.getPlayerName(): String? {
        // check if the value is cached
        uuidPlayerCache[this]?.let { return it.addToMapAndReturn(this) }

        // if not, check if the player is in the world
        this.getGameProfileFromWorld()?.name?.let { return it.addToMapAndReturn(this) }

        // if not, check the mojang api
        val name = this.getNameFromMojang()
        name?.addToMapAndReturn(this)
        return name
    }

    private fun String.addToMapAndReturn(uuid: UUID): String? {
        val newMap = uuidPlayerCache.toMutableMap()
        newMap.put(uuid, this)
        uuidPlayerCache = newMap.toMap()
        return this
    }

    suspend fun String.getPlayerUUID(): UUID? {
        // check if the value is cached
        uuidPlayerCache.entries.firstOrNull { it.value == this }?.key?.let { return it.addToMapAndReturn(this) }

        // if not, check if the player is in the world
        this.getGameProfileFromWorld()?.id?.let { return it.addToMapAndReturn(this) }

        // if not, check the mojang api
        val uuid = this.getUUIDFromMojang()
        uuid?.addToMapAndReturn(this)
        return uuid
    }

    private fun UUID.addToMapAndReturn(name: String): UUID? {
        val newMap = uuidPlayerCache.toMutableMap()
        newMap.put(this, name)
        uuidPlayerCache = newMap.toMap()
        return this
    }
}