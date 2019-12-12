package top.easelink.lcg.network

import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import top.easelink.lcg.ui.main.source.checkLoginState
import top.easelink.lcg.ui.main.source.checkMessages
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies
import top.easelink.lcg.utils.setCookies

object Client {

    private var lastTime = 0L
    private const val CHECK_INTERVAL = 30 * 1000
    private const val BASE_URL = SERVER_BASE_URL

    fun sendRequestWithQuery(query: String): Document {
        return Jsoup
            .connect("$BASE_URL$query")
            .timeout(20* 1000)
            .ignoreHttpErrors(true)
            .cookies(getCookies())
            .method(Connection.Method.GET)
            .execute()
            .let {
                setCookies(it.cookies())
                it.parse().also {
                    checkResponse(it)
                }
            }
    }

    fun sendRequestWithUrl(url: String): Document {
        return Jsoup
            .connect(url)
            .timeout(20* 1000)
            .ignoreHttpErrors(true)
            .cookies(getCookies())
            .method(Connection.Method.GET)
            .execute()
            .let {
                setCookies(it.cookies())
                it.parse().also {
                    checkResponse(it)
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