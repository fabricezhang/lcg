package top.easelink.lcg.ui.main.follow.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import timber.log.Timber
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.follow.model.FeedInfo
import top.easelink.lcg.utils.WebsiteConstant.FOLLOW_FEED_QUERY

class FollowingFeedViewModel : ViewModel() {

    val follows = MutableLiveData<List<FeedInfo>>()
    val isLoading = MutableLiveData<Boolean>()
    val isLoadingForLoadMore = MutableLiveData<Boolean>()
    var pageNum = 1

    fun fetchData() {
        val url = String.format(FOLLOW_FEED_QUERY, 1, 1)
        isLoading.value = true
        GlobalScope.launch(IOPool) {
            try {
                parseFeeds(Client.sendAjaxRequest(url))
            } catch (e: Exception) {
                Timber.e(e)
            }
            isLoading.postValue(false)
        }
    }

    fun fetchMore(callBack: (Boolean) -> Unit) {
        isLoadingForLoadMore.postValue(true)
        val url = String.format(FOLLOW_FEED_QUERY, pageNum, 1)
        GlobalScope.launch(IOPool){
            try {
                parseFeeds(Client.sendAjaxRequest(url))
                    .also(callBack)
                    .takeIf { it }
                    .let {
                        pageNum += 1
                    }
            } catch (e: Exception) {
                Timber.e(e)
            }
            isLoadingForLoadMore.postValue(false)
        }
    }

    private fun parseFeeds(xml: String): Boolean {
        val result = xml
            .substring(xml.indexOf("[CDATA[", 0, true) + 7)
            .removeSuffix("]]></root>")
        if (result == "false") return false
        Jsoup.parse(result)?.apply {
            val feedInfos = select("li.cl").map {
                val avatarUrl = it.selectFirst("a.z > img")?.attr("src").orEmpty()
                var username = ""
                var dateTime = ""
                it.selectFirst("div.flw_author")?.let { author ->
                    username = author.selectFirst("a")?.text().orEmpty()
                    dateTime = author.selectFirst("span")?.text().orEmpty()
                }
                val title = it.selectFirst("h2").text()
                val articleUrl = it.selectFirst("h2 > a").attr("href")
                val followImages = it.getElementsByClass("flw_image")
                    ?.select("ul > li")
                    ?.map { li ->
                        li.selectFirst("img").attr("src")
                    }
                val content = it.selectFirst(".pbm").let { pbm ->
                    pbm.select("div.flw_image")?.remove() // 标题带的图片单独处理，此处删除
                    pbm.select("img")?.remove() // 删除所有图片
                    pbm.select("a.flw_readfull")?.remove() // 删除查看全文链接
                    pbm.html()
                }
                // TODO add reply and relay
                val forum = it.selectFirst("div.xg1 > a ").text()
                val quote = it.selectFirst("div.flw_quotenote")?.text().orEmpty()
                FeedInfo(
                    avatar = avatarUrl,
                    username = username,
                    dateTime = dateTime,
                    title = title,
                    articleUrl = articleUrl,
                    content = content,
                    forum = forum,
                    quote = quote,
                    images = followImages
                )
            }
            follows.postValue(feedInfos)
        }
        return true
    }

}