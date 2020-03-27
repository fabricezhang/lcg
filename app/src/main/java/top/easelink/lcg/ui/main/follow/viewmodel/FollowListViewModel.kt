package top.easelink.lcg.ui.main.follow.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.framework.threadpool.ApiPool
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.follow.model.FollowInfo

class FollowListViewModel : ViewModel() {

    val follows = MutableLiveData<List<FollowInfo>>()
    val isLoading = MutableLiveData<Boolean>()

    fun fetchData(url: String) {
        isLoading.value = true
        GlobalScope.launch(ApiPool) {
            try {
                parseFollows(Client.sendGetRequestWithQuery(url))
            } catch (e: Exception) {
                Timber.e(e)
            }
            isLoading.postValue(false)
        }
    }

    private fun parseFollows(doc: Document) {
        doc.apply {
            val followInfos = select("li.cl").map {
                val avatarUrl = it.selectFirst("img")?.attr("src").orEmpty()
                val username = it.getElementById("edit_avt")?.attr("title").orEmpty()
                val lastAction =  it.selectFirst("p").text()
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
            follows.postValue(followInfos)
        }

    }

}