package top.easelink.lcg.ui.main.source

import android.text.TextUtils
import androidx.annotation.WorkerThread
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import timber.log.Timber
import top.easelink.framework.threadpool.BackGroundPool
import top.easelink.framework.threadpool.CommonPool
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.ui.main.me.model.UserInfo
import top.easelink.lcg.ui.main.model.NewMessageEvent
import top.easelink.lcg.ui.main.model.NotificationInfo

fun checkLoginState(doc: Document) {
    GlobalScope.launch(CommonPool) {
        val userInfo = parseUserInfo(doc)
        UserData.loggedInState = userInfo.userName?.isNotEmpty()?:false
    }
}

@WorkerThread
fun parseUserInfo(doc: Document): UserInfo {
    with(doc) {
        val userName = getElementsByClass("vwmy")?.first()?.firstElementSibling()?.text()
        return if (!TextUtils.isEmpty(userName)) {
            val avatar = selectFirst("div.avt > a > img")?.attr("src")
            val groupInfo = getElementById("g_upmine")?.text()
            getElementsByClass("xi2")?.remove()
            val coin = getElementsByClass("xi1 cl")?.first()?.text()
            val element: Element? = selectFirst("span.xg1")
            val parentCredit = element?.parent()
            element?.remove()
            val credit = parentCredit?.text()
            val signInState = select("img.qq_bind")
                ?.firstOrNull {
                    !(it.attr("src")?.contains("qq")?:true)
                }
                ?.attr("src")
            UserInfo(userName, avatar, groupInfo, coin, credit, signInState)
        } else {
            UserInfo(getElementById("messagetext")?.text())
        }
    }
}

fun checkMessages(doc: Document) {
    GlobalScope.launch(BackGroundPool) {
        val notificationInfo = parseNotificationInfo(doc)
        if (notificationInfo.isNotEmpty()) {
            EventBus.getDefault().post(NewMessageEvent(notificationInfo))
        }
    }
}

@WorkerThread
fun parseNotificationInfo(doc: Document): NotificationInfo {
    with(doc) {
        val menu: Element? = getElementById("myprompt_menu")
        val requestList = mutableListOf<String>()
        var message = 0
        var follower = 0
        var myPost = 0
        var systemNotifs = 0
        menu?.select("li")?.forEach {
            try {
                it.select("a > span").first()?.text()?.also { v ->
                    Timber.d(it.toString())
                    if (v.isNotBlank() && v.toInt() >= 1) {
                        it.selectFirst("a")?.attr("href")?.also { url ->
                            requestList.add(url)
                            when {
                                url.contains("mypost") -> myPost++
                                url.contains("follower") -> follower++
                                url.contains("pm") -> message++
                                url.contains("system") -> systemNotifs++
                                else -> {
                                    // do nothing
                                }
                            }
                        }
                    }
                }
            } catch (e :NumberFormatException) {
                // don't care
            }
        }
        return NotificationInfo(message, follower, myPost, systemNotifs)
    }
}