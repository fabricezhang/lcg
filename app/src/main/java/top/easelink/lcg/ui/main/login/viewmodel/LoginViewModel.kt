package top.easelink.lcg.ui.main.login.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import top.easelink.lcg.utils.WebsiteConstant.LOGIN_URL
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL

class LoginViewModel: ViewModel() {


    fun fetchLoginPage() {
        GlobalScope.launch(Dispatchers.IO) {
            val doc = Jsoup.connect(SERVER_BASE_URL + LOGIN_URL).get()
            val html = doc?.html()?.also {
                parseHtml(it)
            }

        }

    }

    private fun parseHtml(html: String) {

    }
}