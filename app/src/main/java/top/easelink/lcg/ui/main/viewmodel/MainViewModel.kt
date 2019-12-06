package top.easelink.lcg.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import top.easelink.lcg.BuildConfig

class MainViewModel: ViewModel() {

    init {
        updateAppVersion("version: ${BuildConfig.VERSION_NAME}")
    }

    val appVersion = MutableLiveData<String>()

    fun updateAppVersion(version: String) {
        appVersion.postValue(version)
    }
}