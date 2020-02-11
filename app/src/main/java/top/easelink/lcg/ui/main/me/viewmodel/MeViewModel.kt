package top.easelink.lcg.ui.main.me.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import top.easelink.framework.base.BaseFragment
import top.easelink.framework.threadpool.ApiPool
import top.easelink.lcg.network.Client
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.ui.main.me.model.UserInfo
import top.easelink.lcg.ui.main.source.parseUserInfo
import top.easelink.lcg.utils.WebsiteConstant.HOME_QUERY
import top.easelink.lcg.utils.clearCookies
import java.lang.ref.WeakReference

class MeViewModel: ViewModel() {

    private var mFragment: WeakReference<BaseFragment<*,*>>? = null

    val mLoginState = MutableLiveData<Boolean>()
    val mUserInfo = MutableLiveData<UserInfo>()


    fun setFragment(fragment: BaseFragment<*,*>) {
        mFragment = WeakReference(fragment)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    fun fetchUserInfoDirect() {
        if(UserData.loggedInState) {
            mLoginState.postValue(true)
            UserData.apply {
                mUserInfo.value =
                    UserInfo(
                        userName = username,
                        avatarUrl = avatar,
                        wuaiCoin = coin,
                        credit = credit,
                        groupInfo = group,
                        signInStateUrl = null
                    )
            }
        }
        GlobalScope.launch(ApiPool) {
            try {
                val userInfo = Client
                    .sendGetRequestWithQuery(HOME_QUERY).let {
                        parseUserInfo(it)
                    }
                // login failed
                if (userInfo.userName.isNullOrEmpty()) {
                    clearCookies()
                    UserData.clearAll()
                    mLoginState.postValue(false)
                } else if (mUserInfo.value != userInfo) {
                    // login successfully but userInfo not changed
                    mLoginState.postValue(true)
                    mUserInfo.postValue(userInfo)
                    UserData.loggedInState = true
                    UserData.apply {
                        loggedInState = true
                        username = userInfo.userName.toString()
                        avatar = userInfo.avatarUrl.orEmpty()
                        coin = userInfo.wuaiCoin.orEmpty()
                        credit = userInfo.credit.orEmpty()
                        group = userInfo.groupInfo.orEmpty()
                        signInState = userInfo.signInStateUrl.orEmpty()
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
                mLoginState.postValue(false)
            }
        }
    }

}
