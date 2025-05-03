package at.raven.ravenAddons.utils

import at.raven.ravenAddons.loadmodule.LoadModule
import at.raven.ravenAddons.ravenAddons
import com.google.gson.*
import java.net.URL
import java.security.KeyStore
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

@LoadModule
object APIUtils {
    private val ctx: SSLContext? = run {
        try {
            val myKeyStore = KeyStore.getInstance("JKS")
            myKeyStore.load(this.javaClass.getResourceAsStream("/ra-keystore.jks"), "changeit".toCharArray())
            val kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            kmf.init(myKeyStore, null)
            tmf.init(myKeyStore)
            SSLContext.getInstance("TLS").apply {
                init(kmf.keyManagers, tmf.trustManagers, null)
            }
        } catch (e: Exception) {
            println("Failed to load keystore. A lot of API requests won't work")
            e.printStackTrace()
            null
        }
    }

    fun HttpsURLConnection.patchHttpsRequest() {
        ctx?.let {
            this.sslSocketFactory = it.socketFactory
        }
    }

    fun URL.getJsonElementResponse(): JsonElement? {
        val connection = openConnection() as HttpsURLConnection
        connection.patchHttpsRequest()
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "ravenAddons/${ravenAddons.MOD_VERSION}")
        connection.connect()

        val responseText = connection.inputStream.bufferedReader().use { it.readText() }
        return try {
            JsonParser().parse(responseText)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            ChatUtils.warning("Failed to parse response as JSON.")
            println(this.toString())
            null
        } finally {
            connection.disconnect()
        }
    }

    fun URL.getJsonObjectResponse(): JsonObject? = this.getJsonElementResponse()?.asJsonObject
    fun URL.getJsonArrayResponse(): JsonArray? = this.getJsonElementResponse()?.asJsonArray

    fun getJsonElementResponse(url: String) = URL(url).getJsonElementResponse()
    fun getJsonObjectResponse(url: String) = URL(url).getJsonObjectResponse()
    fun getJsonArrayResponse(url: String) = URL(url).getJsonArrayResponse()
}