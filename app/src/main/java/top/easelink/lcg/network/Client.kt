package top.easelink.lcg.network

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import top.easelink.lcg.ui.main.source.checkLoginState
import top.easelink.lcg.ui.main.source.checkMessages
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies

object Client {

    private const val BASE_URL = SERVER_BASE_URL

    fun sendRequestWithQuery(query: String): Document {
        val doc = Jsoup
            .connect("$BASE_URL$query")
            .cookies(getCookies())
            .ignoreHttpErrors(true)
            .get()
        checkLoginState(doc)
        checkMessages(doc)
        return doc
    }

    fun sendRequestWithUrl(url: String): Document {
        val doc = Jsoup
            .connect(url)
            .cookies(getCookies())
            .ignoreHttpErrors(true)
            .get()
        if (url.startsWith(BASE_URL)) {
            checkLoginState(doc)
            checkMessages(doc)
        }
        return doc
    }
}