package top.easelink.lcg.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import top.easelink.framework.threadpool.CommonPool
import top.easelink.lcg.ui.profile.source.ProfileSource

class ProfileViewModel: ViewModel() {

    private var job = Job()

    fun startFetchUserInfo(query: String) {
        val deffer = viewModelScope.async(CommonPool) {
            ProfileSource.getProfile(query)
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (!job.complete()) {
            job.cancel()
        }
    }
}