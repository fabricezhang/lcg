package top.easelink.lcg.ui.main.me.viewmodel

import android.text.TextUtils
import android.view.View
import android.webkit.JavascriptInterface
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import com.bumptech.glide.Glide
import org.jsoup.Jsoup
import top.easelink.framework.base.BaseViewModel
import top.easelink.framework.utils.rx.SchedulerProvider
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
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
        if (v is SwitchCompat) {
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

    fun fetchUserInfo() {
        setIsLoading(true)
        WebViewWrapper.getInstance()
            .loadUrl("$SERVER_BASE_URL$HOME_URL?mod=spacecp&ac=credit&showcredit=1", ::parseHtml)
    }

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
                mUserInfo.postValue(UserInfo(userName, avatar, groupInfo, coin, credit))
            } else{
                Toast.makeText(
                    LCGApp.getContext(),
                    getElementById("messagetext").text(),
                    Toast.LENGTH_SHORT)
                    .show()
                disableAutoSign()
            }
        }
        postIsLoading(false)
    }

    private fun disableAutoSign() {
        WorkManager.getInstance().cancelAllWorkByTag(SignInWorker::class.java.simpleName)
        SharedPreferencesHelper.getUserSp().edit().putBoolean(SP_KEY_AUTO_SIGN_IN, false).commit()
        mAutoSignInEnable.postValue(false)
    }
}
