package top.easelink.lcg.ui.main.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import top.easelink.lcg.BuildConfig

class MainViewModel: ViewModel() {

    val appVersion = MutableLiveData<String>().apply {
        value = "version: ${BuildConfig.VERSION_NAME}"
    }
}