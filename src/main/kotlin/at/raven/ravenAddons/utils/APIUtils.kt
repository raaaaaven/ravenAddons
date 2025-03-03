package at.raven.ravenAddons.utils

import at.raven.ravenAddons.loadmodule.LoadModule
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

    fun patchHttpsRequest(connection: HttpsURLConnection) {
        ctx?.let {
            connection.sslSocketFactory = it.socketFactory
        }
    }
}