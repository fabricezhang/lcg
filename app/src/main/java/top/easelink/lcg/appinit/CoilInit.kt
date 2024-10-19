package top.easelink.lcg.appinit

import android.app.Application
import coil.Coil
import coil.ImageLoader
import coil.ImageLoaderFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import top.easelink.lcg.utils.getCookies
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

fun initCoil() {
    Coil.setImageLoader(LCGImageLoaderFactory)
}

object LCGImageLoaderFactory: ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {

        return ImageLoader
            .Builder(LCGApp.context)
            .okHttpClient(createUnsafeOkHttpClient())
            .build()
    }

    fun createUnsafeOkHttpClient(): OkHttpClient {
        // 创建一个不进行 SSL 校验的 TrustManager
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out java.security.cert.X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<out java.security.cert.X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> = arrayOf()
        })

        // 创建 SSL 上下文
        val sslContext = SSLContext.getInstance("SSL").apply {
            init(null, trustAllCerts, java.security.SecureRandom())
        }

        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            if (originalRequest.url.host.contains("attach")) {
                val newRequest =
                    originalRequest.newBuilder().header("Host", "attach.52pojie.cn") // 添加自定义请求头
                        .header("Referer", "https://www.52pojie.cn/")
                        .header("Sec-Fetch-Site", "same-site").header("Sec-Fetch-Mode", "no-cors")
                        .header("Sec-Fetch-Dest", "image")
                        .header("X-Requested-With", "top.easelink.lcg")
                        .header("Cookie", getCookies().toHeaderString())
                        .header("Connection", "keep-alive").header(
                            "Accept",
                            "image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8"
                        ).header("Accept-Encoding", "gzip, deflate").header(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
                        ).build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(originalRequest)
            }
        }

        // 创建 OkHttpClient
        return OkHttpClient.Builder()
            .addInterceptor(headerInterceptor)
            .followRedirects(true)
            .followSslRedirects(true)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { hostname, session -> true } // 信任所有主机
            .build()
    }

    private fun Map<String, String>.toHeaderString(): String {
        val stringBuilder = StringBuilder()
        forEach {
            stringBuilder.append("${it.key}=${it.value}; ")
        }
        return stringBuilder.toString()
    }

}