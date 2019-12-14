package top.easelink.lcg.ui.main.me.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.nodes.Document
import timber.log.Timber
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.model.SystemNotification
import top.easelink.lcg.utils.WebsiteConstant.SYSTEM_NOTIFICATION_URL

class NotificationViewModel: ViewModel(){

    val systemNotifications= MutableLiveData<List<SystemNotification>>()

    fun fetchNotifications() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                Client.sendRequestWithQuery(SYSTEM_NOTIFICATION_URL).let {
                    systemNotifications.postValue(parseResponse(it))
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private fun parseResponse(doc: Document): List<SystemNotification> {
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
}