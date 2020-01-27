package top.easelink.lcg.network

import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import top.easelink.lcg.LCGApp
import java.io.File
import java.util.concurrent.TimeUnit

object ApiClient: ApiRequest {

    private var mClient: OkHttpClient
    init {
        val cacheDirectory = File(LCGApp.getContext().cacheDir, "okhttp_cache")
        val cookieJar: ClearableCookieJar =
            PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(LCGApp.getContext()))
        mClient = OkHttpClient
            .Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(CacheControlInterceptor)
            .cookieJar(cookieJar)
            .retryOnConnectionFailure(true)
            .cache(cache = Cache(cacheDirectory, 10 * 1024 * 1024))
            .build()
    }

    override fun sendGetRequestWithQuery(query: String): Document? {
        TODO()
    }


    override fun sendGetRequestWithUrl(url: String): Document? {
        val request = Request.Builder().method("GET", null).url(url).build()
        val response = mClient.newCall(request).execute()
        return if (response.isSuccessful) {
            Jsoup.parse(response.body?.string())
        } else {
            null
        }
    }




}