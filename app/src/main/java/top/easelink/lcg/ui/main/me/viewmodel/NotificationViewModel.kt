package top.easelink.lcg.ui.main.me.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.model.BaseNotification
import top.easelink.lcg.ui.main.model.NotificationModel
import top.easelink.lcg.ui.main.model.SystemNotification
import top.easelink.lcg.utils.WebsiteConstant.NOTIFICATION_HOME_URL

class NotificationViewModel: ViewModel(){

    val notifications= MutableLiveData<NotificationModel>()
    val isLoading = MutableLiveData<Boolean>()
    var nextPageUrl = ""

    fun fetchMoreNotifications() {
        if (nextPageUrl.isEmpty()) return
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Client.sendRequestWithQuery(nextPageUrl).let {
                    notifications.postValue(parseResponse(it))
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    fun fetchNotifications() {
        GlobalScope.launch(Dispatchers.IO) {
            isLoading.postValue(true)
            try {
                Client.sendRequestWithQuery(NOTIFICATION_HOME_URL).let {
                    notifications.postValue(parseResponse(it))
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
            isLoading.postValue(false)
        }
    }

    private fun parseSystemResponse(doc: Document): List<SystemNotification> {
        doc.apply {
            val dateTime = select("span.xg1").map {
                it.text()
            }
            return select("dl.cl").mapIndexed { index, element ->
                SystemNotification(
                    title = element.selectFirst("dd.ntc_body").html(),
                    dateTime = dateTime[index]
                )
            }
        }
    }

    private fun parseResponse(doc: Document): NotificationModel {
        val notifications = doc.select("dl.cl").mapNotNull { element ->
            try {
                BaseNotification(
                    avatar = element.selectFirst("img").attr("src"),
                    content = element.selectFirst("dd.ntc_body").html(),
                    dateTime = element.selectFirst("span.xg1").text()
                )
            } catch (e: Exception) {
                Timber.e(e)
                null
            }
        }
        nextPageUrl = doc.select("a.nxt")?.attr("href").orEmpty()
        return NotificationModel(notifications, nextPageUrl)
    }
}