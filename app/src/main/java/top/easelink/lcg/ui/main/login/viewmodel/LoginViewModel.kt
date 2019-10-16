package top.easelink.lcg.ui.main.login.viewmodel

import android.webkit.JavascriptInterface
import androidx.lifecycle.ViewModel
import org.jsoup.Jsoup
import top.easelink.lcg.service.web.WebViewWrapper
import top.easelink.lcg.utils.WebsiteConstant.LOGIN_URL
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL

class LoginViewModel: ViewModel() {

    fun fetchLoginPage() {
        WebViewWrapper.getInstance().loadUrl("$SERVER_BASE_URL${LOGIN_URL}", ::parseHtml)
    }

    @JavascriptInterface
    fun parseHtml(html: String) {
        Jsoup.parse(html).apply {
            val otpUrl = getElementById("vseccode_cS").selectFirst("[width=130]").attr("src")
        }

    }
}