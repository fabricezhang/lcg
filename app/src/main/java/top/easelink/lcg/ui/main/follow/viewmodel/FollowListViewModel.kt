package top.easelink.lcg.ui.main.follow.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.network.JsoupClient
import top.easelink.lcg.ui.main.follow.model.FollowInfo
import top.easelink.lcg.ui.main.follow.model.FollowResult

class FollowListViewModel : ViewModel() {

    val follows = MutableLiveData<FollowResult>()
    val isLoading = MutableLiveData<Boolean>()
    val isLoadingForLoadMore = MutableLiveData<Boolean>()

    fun fetchData(url: String, isLoadMore: Boolean = false) {
        if (isLoadMore) {
            isLoadingForLoadMore.value = true
        } else {
            isLoading.value = true
        }
        GlobalScope.launch(IOPool) {
            try {
                parseFollows(JsoupClient.sendGetRequestWithQuery(url))
            } catch (e: Exception) {
                Timber.e(e)
            }
            if (isLoadMore) {
                isLoadingForLoadMore.postValue(false)
            } else {
                isLoading.postValue(false)
            }
        }
    }

    private fun parseFollows(doc: Document) {
        doc.apply {
            val followInfos = select("li.cl").map {
                val avatarUrl = it.selectFirst("img")?.attr("src").orEmpty()
                val username = it.getElementById("edit_avt")?.attr("title").orEmpty()
                val lastAction = it.selectFirst("p").text()
                val url = it.selectFirst("a[id^=a_followmod]")?.text().orEmpty()
                var following: Int = 0
                var follower: Int = 0
                it.select("strong.xi2").let { e ->
                    if (e.size == 2) {
                        follower = e[0].text().toInt()
                        following = e[1].text().toInt()
                    }
                }
                FollowInfo(
                    avatar = avatarUrl,
                    lastAction = lastAction,
                    username = username,
                    followerNum = follower,
                    followingNum = following,
                    followOrUnFollowUrl = url
                )
            }
            val nextPageUrl = selectFirst("a.nxt")?.attr("href")
            follows.postValue(FollowResult(followInfos, nextPageUrl))
        }

    }

}