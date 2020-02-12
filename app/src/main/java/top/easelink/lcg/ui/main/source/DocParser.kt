package top.easelink.lcg.ui.main.source

import android.text.TextUtils
import androidx.annotation.WorkerThread
import org.greenrobot.eventbus.EventBus
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import timber.log.Timber
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.ui.main.me.model.UserInfo
import top.easelink.lcg.ui.main.model.NewMessageEvent
import top.easelink.lcg.ui.main.model.NotificationInfo

@WorkerThread
fun checkLoginState(doc: Document) {
    try {
        val userInfo = parseUserInfo(doc)
        UserData.loggedInState = userInfo.userName?.isNotEmpty()?:false
    } catch (e: NullPointerException) {
        // for some page, can't extract user info
        Timber.w(e)
    }
}

@WorkerThread
@Throws(NullPointerException::class)
fun parseUserInfo(doc: Document): UserInfo {
    with(doc) {
        val userName = doc.selectFirst("div.jzyhm")?.text()
        return if (!TextUtils.isEmpty(userName)) {
            val avatar = selectFirst("div.avt > a > img")?.attr("src")
            val groupInfo = getElementById("g_upmine")?.text()
            val coin = selectFirst("ul.creditl")?.apply {
                getElementsByClass("xi2")?.remove()
            }?.getElementsByClass("xi1 cl")?.first()?.text()
            val credit = getElementById("extcreditmenu").text()
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

@WorkerThread
fun checkMessages(doc: Document) {
    val notificationInfo = parseNotificationInfo(doc)
    if (notificationInfo.isNotEmpty()) {
        EventBus.getDefault().post(NewMessageEvent(notificationInfo))
    }
}

@WorkerThread
fun extractFormHash(doc: Document): String? {
    return doc.selectFirst("input[name=formhash]")?.attr("value")
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