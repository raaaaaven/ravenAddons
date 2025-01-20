package at.raven.ravenAddons.utils

import at.raven.ravenAddons.ravenAddons
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicHeader
import java.util.zip.GZIPInputStream

object APIUtils {
    private val parser = JsonParser()

    fun getJSONResponseAsElement(
        urlString: String,
        silentError: Boolean = false,
        gzip: Boolean = false,
    ): JsonElement {
        val client = builder.build()
        try {
            client.execute(HttpGet(urlString)).use { response ->
                val entity = response.entity
                if (entity != null) {
                    val inputStream = if (gzip) {
                        GZIPInputStream(entity.content)
                    } else {
                        entity.content
                    }
                    val retSrc = inputStream.bufferedReader().use { it.readText() }
                    try {
                        return parser.parse(retSrc)
                    } catch (e: JsonSyntaxException) {
                        val name = e.javaClass.name
                        val message = "$name: ${e.message}"
                        if (e.message?.contains("Use JsonReader.setLenient(true)") == true) {
                            println("MalformedJsonException: Use JsonReader.setLenient(true)")
                            println(" - getJSONResponse: '$urlString'")
                            ChatUtils.debug("MalformedJsonException: Use JsonReader.setLenient(true)")
                        } else {
                            ChatUtils.warning("Connection Error: $message")
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: Throwable) {
            if (silentError) {
                throw e
            }
            val name = e.javaClass.name
            val message = "$name: ${e.message}"
            ChatUtils.warning("Connection Error: $message")
            e.printStackTrace()
        } finally {
            client.close()
        }
        return JsonObject()
    }

    private val builder: HttpClientBuilder =
        HttpClients.custom().setUserAgent("ravenAddons/${ravenAddons.MOD_VERSION}")
            .setDefaultHeaders(
                mutableListOf(
                    BasicHeader("Pragma", "no-cache"),
                    BasicHeader("Cache-Control", "no-cache"),
                ),
            )
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .build(),
            )
            .useSystemProperties()
}