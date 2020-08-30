package top.easelink.lcg.network

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import top.easelink.lcg.appinit.LCGApp
import top.easelink.lcg.config.AppConfig.followRedirectsEnable
import top.easelink.lcg.ui.search.model.RequestTooOftenException
import java.io.File
import java.util.concurrent.TimeUnit

object OkApiClient: ApiRequest {

    private const val TIME_OUT = 15L
    private var mClient: OkHttpClient
    init {
        val cacheDirectory = File(LCGApp.context.cacheDir, "okhttp_cache")
        val cookieJar: ClearableCookieJar =
            PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(LCGApp.context))
        mClient = OkHttpClient
            .Builder()
            .callTimeout(TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(CacheControlInterceptor)
            .cookieJar(cookieJar)
            .followRedirects(followRedirects = followRedirectsEnable())
            .retryOnConnectionFailure(true)
            .cache(cache = Cache(cacheDirectory, 10 * 1024 * 1024))
            .build()
    }

    override fun sendGetRequestWithQuery(query: String): Document? {
        TODO()
    }


    override fun sendGetRequestWithUrl(url: String): Document? {
        val request = Request.Builder().get().url(url).build()
        val response = mClient.newCall(request).execute()
        return when(response.code) {
            in 200..299 -> Jsoup.parse(response.body?.string())
            302 -> throw RequestTooOftenException()
            else -> null
        }

    }

    override fun sendPostRequestWithUrl(
        url: String,
        form: MutableMap<String, String>?
    ): Connection.Response {
        TODO("not implemented")
    }


}