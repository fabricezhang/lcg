package top.easelink.lcg.ui.main.source

import androidx.annotation.WorkerThread
import org.greenrobot.eventbus.EventBus
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.lcg.ui.main.model.NewMessageEvent
import top.easelink.lcg.ui.main.model.NotificationInfo

@WorkerThread
fun checkMessages(doc: Document) {
    val notificationInfo = parseNotificationInfo(doc)
    if (notificationInfo.isNotEmpty()) {
        Timber.d("new message arrival")
        EventBus.getDefault().post(NewMessageEvent(notificationInfo))
    }
}

fun parseNotificationInfo(doc: Document): NotificationInfo {
    with(doc) {
        val menu = getElementById("myprompt_menu")
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