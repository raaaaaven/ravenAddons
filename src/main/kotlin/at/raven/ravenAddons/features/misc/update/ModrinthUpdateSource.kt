package at.raven.ravenAddons.features.misc.update

import at.raven.ravenAddons.utils.APIUtils
import com.google.gson.JsonPrimitive
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import moe.nea.libautoupdate.JsonUpdateSource
import moe.nea.libautoupdate.UpdateData
import java.text.SimpleDateFormat
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

class ModrinthUpdateSource(
    project: String,
    loader: String,
    mcVersion: String,
) : JsonUpdateSource() {

    private val url = "https://api.modrinth.com/v2/project/$project/version?loaders=%5B%22$loader%22%5D&game_versions=%5B%22$mcVersion%22%5D"
    private val token = object : TypeToken<List<ModrinthVersion>?>() {
    }

    companion object {
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
    }

    private fun getVersions(): CompletableFuture<List<ModrinthVersion>> {
        return CompletableFuture.supplyAsync {
            try {
                val json = APIUtils.getJSONResponseAsElement(url)
                return@supplyAsync gson.fromJson(json, token.type)
            } catch (e: Exception) {
                e.printStackTrace()
                throw CompletionException(e)
            }
        }
    }

    override fun checkUpdate(updateStream: String?): CompletableFuture<UpdateData> {
        return getVersions().thenApply { releases ->
            val latest = releases.maxByOrNull { dateFormat.parse(it.datePublished) } ?: return@thenApply null
            val file = latest.files.firstOrNull { it.primary } ?: return@thenApply null
            return@thenApply UpdateData(
                latest.name,
                JsonPrimitive(latest.versionNumber),
                null,
                file.url,
            )
        }
    }
}

private data class ModrinthHashes(
    @Expose val sha512: String,
)

private data class ModrinthFile(
    @Expose val url: String,
    @Expose val hashes: ModrinthHashes,
    @Expose val primary: Boolean,
)

private data class ModrinthVersion(
    @Expose val name: String,
    @Expose @SerializedName("version_number") val versionNumber: String,
    @Expose val files: List<ModrinthFile>,
    @Expose @SerializedName("date_published") val datePublished: String,
)