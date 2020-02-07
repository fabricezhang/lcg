package top.easelink.lcg.network

import android.annotation.SuppressLint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.framework.threadpool.BackGroundPool
import top.easelink.lcg.BuildConfig
import top.easelink.lcg.config.AppConfig.followRedirectsEnable
import top.easelink.lcg.ui.main.source.checkLoginState
import top.easelink.lcg.ui.main.source.checkMessages
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies
import top.easelink.lcg.utils.setCookies
import java.net.SocketTimeoutException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

object Client: ApiRequest {

    init {
        if (BuildConfig.DEBUG) {
            trustAll()
        }
    }

    private var lastTime = 0L
    private val CHECK_INTERVAL = if (BuildConfig.DEBUG) 60 * 1000 else 30 * 1000
    private const val TIME_OUT_LIMIT = 15 * 1000
    private const val BASE_URL = SERVER_BASE_URL

    @Throws(SocketTimeoutException::class)
    override fun sendGetRequestWithQuery(query: String): Document {
        return Jsoup
            .connect("$BASE_URL$query")
            .timeout(TIME_OUT_LIMIT)
            .ignoreHttpErrors(true)
            .cookies(getCookies())
            .method(Connection.Method.GET)
            .followRedirects(followRedirectsEnable())
            .execute()
            .let {
                setCookies(it.cookies())
                it.parse().also { doc ->
                    checkResponse(doc)
                }
            }
    }

    override fun sendGetRequestWithUrl(url: String): Document {
        return Jsoup
            .connect(url)
            .timeout(TIME_OUT_LIMIT)
            .ignoreHttpErrors(true)
            .cookies(getCookies())
            .method(Connection.Method.GET)
            .followRedirects(followRedirectsEnable())
            .execute()
            .let {
                setCookies(it.cookies())
                it.parse().also { doc ->
                    checkResponse(doc)
                }
            }
    }

    private fun checkResponse(doc: Document) {
        GlobalScope.launch(BackGroundPool){
            if (System.currentTimeMillis() - lastTime > CHECK_INTERVAL) {
                lastTime = System.currentTimeMillis()
                try {
                    checkLoginState(doc)
                    checkMessages(doc)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    private fun trustAll() {
        try { // Create a trust manager that does not validate certificate chains
            val trustAllCerts =
                arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate>? {
                        return null
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkClientTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) { }

                    @SuppressLint("TrustAllX509TrustManager")
                    override fun checkServerTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) { }
                }
                )
            // Install the all-trusting trust manager
            val sc = SSLContext.getInstance("SSL")
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            // Create all-trusting host name verifier
            val allHostsValid = HostnameVerifier { _, _ -> true }
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }
}