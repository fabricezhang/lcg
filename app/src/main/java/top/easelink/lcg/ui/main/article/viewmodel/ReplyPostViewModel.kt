package top.easelink.lcg.ui.main.article.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import timber.log.Timber
import top.easelink.lcg.network.Client
import top.easelink.lcg.utils.WebsiteConstant.CHECK_RULE_URL
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies
import top.easelink.lcg.utils.setCookies

class ReplyPostViewModel : ViewModel() {

    val sending = MutableLiveData<Boolean>()

    fun sendReply(query: String?, content: String, callback: () -> Unit) {
        sending.value = true
        GlobalScope.launch(Dispatchers.IO) {
            sendReplyAsync(query, content, callback)
        }
    }

    private fun sendReplyAsync(query: String?, content: String, callback: () -> Unit) {
        Timber.d(content)
        if (query.isNullOrEmpty() || content.isBlank()) {
            return
        }
        val queryMap = mutableMapOf<String, String>()
        query
            .split("&")
            .toMutableList()
            .forEach {
                val l = it.split("=")
                if (l.size == 2) {
                    queryMap[l[0]] = l[1]
                } else {
                    queryMap[l[0]] = ""
                }
            }
        try {
            Client
                .sendGetRequestWithQuery(query)
                .run {
                    val noticeauthormsg = selectFirst("input[name=noticeauthormsg]").attr("value")
                    val noticetrimstr = selectFirst("input[name=noticetrimstr]").attr("value")
                    val noticeauthor = selectFirst("input[name=noticeauthor]").attr("value")
                    val handlekey = selectFirst("input[name=handlekey]")?.attr("value")?:"reply"
                    val usesig = selectFirst("input[name=usesig]")?.attr("value")?:"1"
                    val reppid = selectFirst("input[name=reppid]").attr("value")
                    val reppost = selectFirst("input[name=reppost]").attr("value")
                    val formHash = selectFirst("input[name=formhash]").attr("value")
                    var response = Jsoup
                        .connect(CHECK_RULE_URL)
                        .timeout(60* 1000)
                        .cookies(getCookies())
                        .method(Connection.Method.GET)
                        .execute()
                    if (response.statusCode() in 200 until 300) {
                        Timber.d(response.body())
                        setCookies(response.cookies())
                        val url = "${SERVER_BASE_URL}forum.php?mod=post&infloat=yes&action=reply" +
                                "&fid=${queryMap["fid"]}&extra=${queryMap["extra"]}&tid=${queryMap["tid"]}&replysubmit=yes&inajax=1"
                        response = Jsoup
                            .connect(url)
                            .cookies(getCookies())
                            .data(
                                "formhash", formHash,
                                "handlekey", handlekey,
                                "noticeauthor", noticeauthor,
                                "noticetrimstr", noticetrimstr,
                                "noticeauthormsg", noticeauthormsg,
                                "usesig", usesig,
                                "reppid", reppid,
                                "reppost", reppost,
                                "subject", "",
                                "message", content
                            )
                            .postDataCharset("gbk")
                            .method(Connection.Method.POST)
                            .execute()
                        setCookies(response.cookies())
                    } else {
                        Timber.e(response.body())
                    }
                }
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            sending.postValue(false)
            callback.invoke()
        }

    }
}

