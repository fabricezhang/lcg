package top.easelink.lcg.ui.search.source

import androidx.annotation.WorkerThread
import kotlinx.coroutines.delay
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import timber.log.Timber
import top.easelink.lcg.R
import top.easelink.lcg.network.OkApiClient
import top.easelink.lcg.ui.search.model.BaiduSearchResult
import top.easelink.lcg.ui.search.model.BaiduSearchResults
import top.easelink.lcg.ui.search.model.RequestTooOftenException
import top.easelink.lcg.utils.showMessage


object BaiduSearchService {

    suspend fun doSearchRequest(requestUrl: String, retryTime: Int): BaiduSearchResults {
        return try {
            if (retryTime <= 3) {
                doSearchRequest(requestUrl)
            } else {
                BaiduSearchResults(emptyList())
            }
        } catch (e: RequestTooOftenException) {
            delay(1000)
            doSearchRequest(requestUrl, retryTime+1)
        }
    }

    @WorkerThread
    @Throws(RequestTooOftenException::class)
    fun doSearchRequest(requestUrl: String): BaiduSearchResults {
        try {
            val doc = OkApiClient.sendGetRequestWithUrl(requestUrl)
            val list: List<BaiduSearchResult> = doc?.select("div.result")
                ?.map {
                    try {
                        val title = extractFrom(it, "h3.c-title", "a")
                        val url = extractAttrFrom(it, "href", "h3.c-title", "a")
                        val content = extractFrom(it, "div.c-abstract")
                        return@map BaiduSearchResult(title, content, url)
                    } catch (nbe: NumberFormatException) {
                        Timber.v(nbe)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                    null
                }
                ?.filterNotNull()
                .orEmpty()
            if (list.isNullOrEmpty()) {

                return BaiduSearchResults(emptyList())
            }
            return BaiduSearchResults(list).also {
                try {
                    it.nextPageUrl = doc?.selectFirst("a.pager-next-foot")
                        ?.attr("href")
                    it.totalResult = doc
                        ?.getElementsByClass("support-text-top")
                        ?.first()
                        ?.text()
                } catch (e: Exception) { // mute
                    it.nextPageUrl = null
                }
            }
        } catch (e: RequestTooOftenException) {
            throw e
        } catch (e: Exception) {
            Timber.w(e)
            showMessage(R.string.error)
        }
        return BaiduSearchResults(emptyList())
    }

    private fun extractFrom(element: Element, vararg tags: String): String {
        if (tags.isNullOrEmpty()) {
            return element.text()
        }
        var e = Elements(element)
        for (tag in tags) {
            e = e.select(tag)
            if (e.isEmpty()) {
                break
            }
        }
        return e.text()
    }

    private fun extractAttrFrom(element: Element, attr: String, vararg tags: String): String {
        if (tags.isNullOrEmpty()) {
            return element.text()
        }
        var e = Elements(element)
        for (tag in tags) {
            e = e.select(tag)
            if (e.isEmpty()) {
                break
            }
        }
        return e.attr(attr)
    }
}