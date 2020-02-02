package top.easelink.lcg.ui.search.source

import androidx.annotation.WorkerThread
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import timber.log.Timber
import top.easelink.lcg.R
import top.easelink.lcg.network.ApiClient
import top.easelink.lcg.ui.search.model.SearchResult
import top.easelink.lcg.ui.search.model.SearchResults
import top.easelink.lcg.utils.showMessage

/**
 * author : junzhang
 * date   : 2019-07-04 16:22
 * desc   :
 */

object SearchService {

    @WorkerThread
    fun doSearchRequest(requestUrl: String): SearchResults {
        try {
            val doc = ApiClient.sendGetRequestWithUrl(requestUrl)
            val list: List<SearchResult> = doc?.select("div.result")
                ?.map {
                    try {
                        val title = extractFrom(it, "h3.c-title", "a")
                        val url = extractAttrFrom(it, "href", "h3.c-title", "a")
                        val content = extractFrom(it, "div.c-abstract")
                        return@map SearchResult(title, content, url)
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
                return SearchResults(emptyList())
            }
            return SearchResults(list).also {
                try {
                    it.nextPageUrl = doc?.
                        selectFirst("a.pager-next-foot")
                        ?.attr("href")
                    it.totalResult = doc
                        ?.getElementsByClass("support-text-top")
                        ?.first()
                        ?.text()
                } catch (e: Exception) { // mute
                    it.nextPageUrl = null
                }
            }

        } catch (e: Exception) {
            Timber.w(e)
            showMessage(R.string.error)
        }
        return SearchResults(emptyList())
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