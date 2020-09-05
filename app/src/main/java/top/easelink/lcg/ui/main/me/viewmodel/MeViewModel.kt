package top.easelink.lcg.ui.main.me.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.framework.threadpool.IOPool
import top.easelink.lcg.R
import top.easelink.lcg.account.UserDataRepo
import top.easelink.lcg.account.UserInfo
import top.easelink.lcg.ui.main.me.source.UserInfoRepo
import top.easelink.lcg.ui.main.model.AntiScrapingException
import top.easelink.lcg.utils.clearCookies
import top.easelink.lcg.utils.showMessage
import java.net.SocketTimeoutException

class MeViewModel : ViewModel() {

    @Suppress("BlockingMethodInNonBlockingContext")
    fun fetchUserInfoDirect() {
        if (UserDataRepo.isLoggedIn) {
            UserDataRepo.updateUserInfo(
                UserInfo(
                    userName = UserDataRepo.username,
                    avatarUrl = UserDataRepo.avatar,
                    wuaiCoin = UserDataRepo.coin,
                    credit = UserDataRepo.credit,
                    groupInfo = UserDataRepo.group,
                    enthusiasticValue = UserDataRepo.enthusiasticValue,
                    answerRate = UserDataRepo.answerRate,
                    signInStateUrl = null
                )
            )
        }
        viewModelScope.launch(IOPool) {
            try {
                val userInfo = UserInfoRepo.requestUserInfo()
                // login failed
                if (userInfo == null || userInfo.userName.isNullOrEmpty()) {
                    UserDataRepo.clearAll()
                } else {
                    // login successfully or userInfo changed
                    UserDataRepo.isLoggedIn = true
                    UserDataRepo.updateUserInfo(userInfo)
                }
            } catch (e: Exception) {
                Timber.e(e)
                when (e) {
                    is SocketTimeoutException -> R.string.network_error // 网络错误，不认为是登陆异常
                    is AntiScrapingException -> R.string.anti_scraping_error // 针对触发反爬虫机制的处理
                    else -> R.string.general_error
                }.let {
                    showMessage(it)
                }
            }
        }
    }
}
