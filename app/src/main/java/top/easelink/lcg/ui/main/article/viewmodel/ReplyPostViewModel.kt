package top.easelink.lcg.ui.main.article.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Connection
import org.jsoup.Jsoup
import timber.log.Timber
import top.easelink.lcg.utils.WebsiteConstant.CHECK_RULE_URL
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies
import top.easelink.lcg.utils.setCookies

class ReplyPostViewModel : ViewModel() {

    fun sendReply(requestUrl: String?, content: String) {
        GlobalScope.launch(Dispatchers.IO) {
            sendReplyAsync(requestUrl, content)
        }
    }

    private fun sendReplyAsync(requestUrl: String?, content: String) {
        Timber.d(content)
        if (requestUrl.isNullOrEmpty() || content.isBlank()) {
            return
        }
        val queryMap = mutableMapOf<String, String>()
        requestUrl
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
            Jsoup
                .connect(SERVER_BASE_URL + requestUrl)
                .timeout(60 * 1000)
                .cookies(getCookies())
                .get()
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
                                "noticeauthor", String(noticeauthor.toByteArray(charset("gbk"))),
                                "noticetrimstr", String(noticetrimstr.toByteArray(charset("gbk"))),
                                "noticeauthormsg", String(noticeauthormsg.toByteArray(charset("gbk"))),
                                "usesig", usesig,
                                "reppid", reppid,
                                "reppost", reppost,
                                "subject", "",
                                "message", String(content.toByteArray(charset("gbk")))
                            )
                            .method(Connection.Method.POST)
                            .execute()
                        setCookies(response.cookies())
                    } else {
                        Timber.e(response.body())
                    }
                }
        } catch (e: Exception) {
            Timber.e(e)
        }

    }
}

