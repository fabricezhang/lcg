package top.easelink.lcg.ui.main.me.viewmodel

import android.webkit.CookieManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import timber.log.Timber
import top.easelink.framework.base.BaseFragment
import top.easelink.framework.threadpool.ApiPool
import top.easelink.lcg.R
import top.easelink.lcg.spipedata.UserData
import top.easelink.lcg.ui.main.logout.view.LogoutHintDialog
import top.easelink.lcg.ui.main.me.model.UserInfo
import top.easelink.lcg.ui.main.source.parseUserInfo
import top.easelink.lcg.utils.SharedPreferencesHelper
import top.easelink.lcg.utils.WebsiteConstant.HOME_URL
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies
import top.easelink.lcg.utils.showMessage
import java.lang.ref.WeakReference

class MeViewModel: ViewModel() {

    private var mFragment: WeakReference<BaseFragment<*,*>>? = null

    val mLoginState = MutableLiveData<Boolean>()
    val mUserInfo = MutableLiveData<UserInfo>()


    fun setFragment(fragment: BaseFragment<*,*>) {
        mFragment = WeakReference(fragment)
    }

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
                val doc = Jsoup
                    .connect("$SERVER_BASE_URL$HOME_URL?mod=spacecp&ac=credit&showcredit=1")
                    .cookies(getCookies())
                    .ignoreHttpErrors(true)
                    .get()
                val userInfo = parseUserInfo(doc)
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

    fun tryLogout() {
        mFragment?.get()?.apply {
            LogoutHintDialog(
                positive = {
                    UserData.clearAll()
                    clearCookies()
                    showMessage(R.string.me_tab_clear_cookie)
                    activity?.onBackPressed()
                },
                negative = { }
            ).show(
                fragmentManager?:activity!!.supportFragmentManager,
                LogoutHintDialog::class.java.simpleName
            )
        }
    }

    private fun clearCookies() {
        SharedPreferencesHelper.getCookieSp().edit().clear().apply()
        CookieManager.getInstance().removeAllCookies(null)
    }
}
