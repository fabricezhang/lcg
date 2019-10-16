package top.easelink.lcg.ui.main.me.viewmodel

import android.text.TextUtils
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import top.easelink.framework.base.BaseViewModel
import top.easelink.framework.utils.rx.SchedulerProvider
import top.easelink.lcg.LCGApp
import top.easelink.lcg.service.web.WebViewWrapper
import top.easelink.lcg.service.work.SignInWorker
import top.easelink.lcg.service.work.SignInWorker.Companion.DEFAULT_TIME_UNIT
import top.easelink.lcg.service.work.SignInWorker.Companion.WORK_INTERVAL
import top.easelink.lcg.ui.main.me.model.UserInfo
import top.easelink.lcg.ui.main.me.view.MeNavigator
import top.easelink.lcg.ui.main.source.local.SPConstants.SP_KEY_AUTO_SIGN_IN
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper
import top.easelink.lcg.utils.WebsiteConstant.HOME_URL
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies

class MeViewModel(schedulerProvider:SchedulerProvider):BaseViewModel<MeNavigator>(schedulerProvider) {

    private val mUserInfo = MutableLiveData<UserInfo>()
    private val mAutoSignInEnable = MutableLiveData<Boolean>()

    init {
        mAutoSignInEnable.postValue(SharedPreferencesHelper
            .getUserSp()
            .getBoolean(SP_KEY_AUTO_SIGN_IN, false))
    }

    val workInfo:LiveData<List<WorkInfo>>
        get() = WorkManager.getInstance().getWorkInfosByTagLiveData(SignInWorker::class.java.simpleName)

    val userInfo:LiveData<UserInfo>
        get() = mUserInfo

    val autoSignEnable: LiveData<Boolean>
        get() = mAutoSignInEnable

    fun scheduleJob(v:View) {
        if (v is CheckBox) {
            SharedPreferencesHelper.getUserSp()
                .edit()
                .putBoolean(SP_KEY_AUTO_SIGN_IN, v.isChecked)
                .apply()
            if (v.isChecked) {
                val constraints = Constraints.Builder()
                    .setRequiresDeviceIdle(false)
                    .setRequiresCharging(false)
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(false)
                    .build()
                val request = PeriodicWorkRequest.Builder(SignInWorker::class.java, WORK_INTERVAL, DEFAULT_TIME_UNIT)
                    .setConstraints(constraints)
                    .addTag(SignInWorker::class.java.simpleName)
                    .build()
                WorkManager.getInstance().enqueue(request)
            }
            else {
                WorkManager.getInstance().cancelAllWorkByTag(SignInWorker::class.java.simpleName)
            }
        }
    }

    /**
     * Used as a back up of Jsoup
     */
    fun fetchUserInfo() {
        setIsLoading(true)
        WebViewWrapper.getInstance()
            .loadUrl("$SERVER_BASE_URL$HOME_URL?mod=spacecp&ac=credit&showcredit=1", ::parseHtml)
    }

    fun fetchUserInfoDirect() {
        setIsLoading(true)
        GlobalScope.launch(Dispatchers.IO) {
            val response = Jsoup
                .connect("$SERVER_BASE_URL$HOME_URL?mod=spacecp&ac=credit&showcredit=1")
                .cookies(getCookies())
                .get()
                .html()
            val userInfo = parse(response)
            if (userInfo.userName.isNullOrEmpty()) {
                disableAutoSign()
                clearCookies()
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        LCGApp.getContext(),
                        userInfo.messageText,
                        Toast.LENGTH_SHORT)
                        .show()
                    navigator.showLoginFragment()
                    setIsLoading(false)
                }
            } else {
                mUserInfo.postValue(userInfo)
                postIsLoading(false)
            }
        }
    }

    @WorkerThread
    private fun clearCookies() {
        SharedPreferencesHelper.getCookieSp().edit().clear().commit()
    }

    /**
     * Used as a back up of Jsoup
     */
    @JavascriptInterface
    fun parseHtml(html: String) {
        Jsoup.parse(html).apply {
            val userName = getElementsByClass("vwmy")?.first()?.firstElementSibling()?.text()
            if (!TextUtils.isEmpty(userName)) {
                val avatar = selectFirst("div.avt > a > img")?.attr("src")
                val groupInfo = getElementById("g_upmine")?.text()
                getElementsByClass("xi2")?.remove()
                val coin = getElementsByClass("xi1 cl")?.first()?.text()
                val element = selectFirst("span.xg1")
                val parentCredit = element?.parent()
                element?.remove()
                val credit = parentCredit?.text()
                val signInState = selectFirst("img.qq_bind")?.attr("src")
                mUserInfo.postValue(UserInfo(userName, avatar, groupInfo, coin, credit, signInState))
            } else{
                Toast.makeText(
                    LCGApp.getContext(),
                    getElementById("messagetext").text(),
                    Toast.LENGTH_SHORT)
                    .show()
                disableAutoSign()
                navigator.showLoginFragment()
            }
        }
        postIsLoading(false)
    }


    private fun parse(html: String): UserInfo {
        Jsoup.parse(html).apply {
            val userName = getElementsByClass("vwmy")?.first()?.firstElementSibling()?.text()
            if (!TextUtils.isEmpty(userName)) {
                val avatar = selectFirst("div.avt > a > img")?.attr("src")
                val groupInfo = getElementById("g_upmine")?.text()
                getElementsByClass("xi2")?.remove()
                val coin = getElementsByClass("xi1 cl")?.first()?.text()
                val element = selectFirst("span.xg1")
                val parentCredit = element?.parent()
                element?.remove()
                val credit = parentCredit?.text()
                val signInState = selectFirst("img.qq_bind")?.attr("src")
                return UserInfo(userName, avatar, groupInfo, coin, credit, signInState)
            } else {
                return UserInfo(getElementById("messagetext").text())
            }
        }
    }

    private fun disableAutoSign() {
        WorkManager.getInstance().cancelAllWorkByTag(SignInWorker::class.java.simpleName)
        SharedPreferencesHelper.getUserSp().edit().putBoolean(SP_KEY_AUTO_SIGN_IN, false).commit()
        mAutoSignInEnable.postValue(false)
    }
}
