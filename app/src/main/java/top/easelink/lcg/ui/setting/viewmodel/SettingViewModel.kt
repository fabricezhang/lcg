package top.easelink.lcg.ui.setting.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import top.easelink.lcg.config.AppConfig
import top.easelink.lcg.mta.*
import top.easelink.lcg.service.work.SignInWorker
import java.util.*

class SettingViewModel : ViewModel() {
    val autoSignInEnable = MutableLiveData<Boolean>()
    val syncFavoriteEnable = MutableLiveData<Boolean>()
    val searchEngineSelected = MutableLiveData<Int>()
    val openSearchResultInWebView = MutableLiveData<Boolean>()
    val openArticleInWebView = MutableLiveData<Boolean>()
    val showRecommendFlag = MutableLiveData<Boolean>()

    fun init() {
        with(AppConfig) {
            autoSignInEnable.value = autoSignEnable
            syncFavoriteEnable.value = syncFavorites
            searchEngineSelected.value = defaultSearchEngine
            openSearchResultInWebView.value = searchResultShowInWebView
            openArticleInWebView.value = articleShowInWebView
            showRecommendFlag.value= articleShowRecommendFlag
        }
    }

    fun scheduleJob(enable: Boolean) {
        AppConfig.autoSignEnable = enable
        if (enable) {
            SignInWorker.startSignInWork()
        } else {
            WorkManager.getInstance().cancelAllWorkByTag(SignInWorker.TAG)
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