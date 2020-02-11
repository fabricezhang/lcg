package top.easelink.lcg.ui.setting.viewmodel

import android.webkit.CookieManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import top.easelink.lcg.R
import top.easelink.lcg.config.AppConfig
import top.easelink.lcg.mta.*
import top.easelink.lcg.service.work.SignInWorker
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.ui.main.logout.view.LogoutHintDialog
import top.easelink.lcg.utils.SharedPreferencesHelper
import top.easelink.lcg.utils.showMessage
import java.util.*

class SettingViewModel : ViewModel() {
    val autoSignInEnable = MutableLiveData<Boolean>()
    val syncFavoriteEnable = MutableLiveData<Boolean>()
    val searchEngineSelected = MutableLiveData<Int>()
    val openSearchResultInWebView = MutableLiveData<Boolean>()

    fun init() {
        autoSignInEnable.postValue(AppConfig.autoSignEnable)
        syncFavoriteEnable.postValue(AppConfig.syncFavorites)
        searchEngineSelected.postValue(AppConfig.defaultSearchEngine)
        openSearchResultInWebView.postValue(AppConfig.searchResultShowInWebView)
    }

    fun scheduleJob(enable: Boolean) {
        AppConfig.autoSignEnable = enable
        if (enable) {
            val constraints = Constraints.Builder()
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(false)
                .build()
            val request = PeriodicWorkRequest.Builder(
                SignInWorker::class.java,
                SignInWorker.WORK_INTERVAL,
                SignInWorker.DEFAULT_TIME_UNIT
            )
                .setConstraints(constraints)
                .addTag(SignInWorker::class.java.simpleName)
                .build()
            WorkManager.getInstance().enqueue(request)
        } else {
            WorkManager.getInstance().cancelAllWorkByTag(SignInWorker::class.java.simpleName)
        }
        sendKVEvent(EVENT_AUTO_SIGN, Properties().apply {
            setProperty(PROP_IS_AUTO_SIGN_ENABLE, enable.toString())
        })
    }

    fun setSyncFavorite(enable: Boolean) {
        AppConfig.syncFavorites = enable
        sendKVEvent(EVENT_SYNC_FAVORITE, Properties().apply {
            setProperty(
                PROP_IS_SYNC_FAVORITE_ENABLE,
                enable.toString()
            )
        })
    }

}