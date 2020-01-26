package top.easelink.lcg.network

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import top.easelink.framework.utils.NetworkUtils
import top.easelink.lcg.LCGApp

object CacheControlInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
            if (!NetworkUtils.isNetworkConnected(LCGApp.getContext())) {
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            }
            val originalResponse = chain.proceed(request)
            return if (NetworkUtils.isNetworkConnected(LCGApp.getContext())) {
                originalResponse.newBuilder()
                    .header("Cache-Control", request.cacheControl.toString())
                    .removeHeader("Pragma")
                    .build()
            } else {
                val maxStale = 7 * 24 * 60 * 60
                originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("Pragma")
                    .build()
            }
        }
    }