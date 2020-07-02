package top.easelink.lcg.ui.main.me.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.framework.base.BaseFragment
import top.easelink.framework.threadpool.ApiPool
import top.easelink.lcg.R
import top.easelink.lcg.network.Client
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.ui.main.me.model.UserInfo
import top.easelink.lcg.ui.main.model.AntiScrapingException
import top.easelink.lcg.ui.main.source.parseUserInfo
import top.easelink.lcg.utils.WebsiteConstant.PROFILE_QUERY
import top.easelink.lcg.utils.clearCookies
import top.easelink.lcg.utils.showMessage
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException

class MeViewModel: ViewModel() {

    private var mFragment: WeakReference<BaseFragment<*,*>>? = null
    private var isResolvingAntiScrapingException: Boolean = false

    val mLoginState = MutableLiveData<Boolean>()
    val mUserInfo = MutableLiveData<UserInfo>()

    fun setFragment(fragment: BaseFragment<*,*>) {
        mFragment = WeakReference(fragment)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun fetchUserInfoDirect() {
        if(UserData.isLoggedIn) {
            mLoginState.postValue(true)
            UserData.apply {
                mUserInfo.value =
                    UserInfo(
                        userName = username,
                        avatarUrl = avatar,
                        wuaiCoin = coin,
                        credit = credit,
                        groupInfo = group,
                        enthusiasticValue = enthusiasticValue,
                        answerRate = answerRate,
                        signInStateUrl = null
                    )
            }
        }
        GlobalScope.launch(ApiPool) {
            try {
                val userInfo = Client
                    .sendGetRequestWithQuery(PROFILE_QUERY).let {
                        parseUserInfo(it)
                    }
                // login failed
                if (userInfo.userName.isNullOrEmpty()) {
                    clearCookies()
                    UserData.clearAll()
                    mLoginState.postValue(false)
                } else if (mUserInfo.value != userInfo) {
                    // login successfully but userInfo changed
                    mLoginState.postValue(true)
                    mUserInfo.postValue(userInfo)
                    UserData.isLoggedIn = true
                    UserData.apply {
                        isLoggedIn = true
                        username = userInfo.userName.toString()
                        avatar = userInfo.avatarUrl.orEmpty()
                        coin = userInfo.wuaiCoin.orEmpty()
                        credit = userInfo.credit.orEmpty()
                        group = userInfo.groupInfo.orEmpty()
                        enthusiasticValue = userInfo.enthusiasticValue.orEmpty()
                        answerRate = userInfo.answerRate.orEmpty()
                        signInState = userInfo.signInStateUrl.orEmpty()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                when(e) {
                    is SocketTimeoutException -> showMessage(R.string.network_error) // 网络错误，不认为是登陆异常
                    is AntiScrapingException -> showMessage(R.string.anti_scraping_error) // 针对触发反爬虫机制的处理
                    else -> {
                        mLoginState.postValue(false)
                    }
                }
            }
        }
    }
}
