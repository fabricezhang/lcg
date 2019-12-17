package top.easelink.lcg.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import top.easelink.lcg.BuildConfig
import top.easelink.lcg.ui.main.source.checkLoginState
import top.easelink.lcg.ui.main.source.checkMessages
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies
import top.easelink.lcg.utils.setCookies

object Client {

    private var lastTime = 0L
    private val CHECK_INTERVAL = if (BuildConfig.DEBUG) 60 * 1000 else 30 * 1000
    private const val TIME_OUT_LIMIT = 15 * 1000
    private const val BASE_URL = SERVER_BASE_URL

    fun sendRequestWithQuery(query: String): Document {
        return Jsoup
            .connect("$BASE_URL$query")
            .timeout(TIME_OUT_LIMIT)
            .ignoreHttpErrors(true)
            .cookies(getCookies())
            .method(Connection.Method.GET)
            .execute()
            .let {
                setCookies(it.cookies())
                it.parse().also { doc ->
                    checkResponse(doc)
                }
            }
    }

    fun sendRequestWithUrl(url: String): Document {
        return Jsoup
            .connect(url)
            .timeout(TIME_OUT_LIMIT)
            .ignoreHttpErrors(true)
            .cookies(getCookies())
            .method(Connection.Method.GET)
            .execute()
            .let {
                setCookies(it.cookies())
                it.parse().also { doc ->
                    GlobalScope.launch(Dispatchers.IO){
                        checkResponse(doc)
                    }
                }
            }
    }

    private fun checkResponse(doc: Document) {
        if (System.currentTimeMillis() - lastTime > CHECK_INTERVAL) {
            lastTime = System.currentTimeMillis()
            checkLoginState(doc)
            checkMessages(doc)
        }
    }
}