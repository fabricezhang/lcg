package top.easelink.lcg.ui.main.me.viewmodel

import android.annotation.SuppressLint
import android.view.View
import androidx.annotation.WorkerThread
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import timber.log.Timber
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.R
import top.easelink.lcg.mta.*
import top.easelink.lcg.service.work.SignInWorker
import top.easelink.lcg.service.work.SignInWorker.Companion.DEFAULT_TIME_UNIT
import top.easelink.lcg.service.work.SignInWorker.Companion.WORK_INTERVAL
import top.easelink.lcg.ui.info.UserData
import top.easelink.lcg.ui.main.logout.view.LogoutHintDialog
import top.easelink.lcg.ui.main.me.model.UserInfo
import top.easelink.lcg.ui.main.source.local.SP_KEY_AUTO_SIGN_IN
import top.easelink.lcg.ui.main.source.local.SP_KEY_SYNC_FAVORITE
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper
import top.easelink.lcg.ui.main.source.parseUserInfo
import top.easelink.lcg.utils.WebsiteConstant.HOME_URL
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.getCookies
import top.easelink.lcg.utils.showMessage
import java.lang.ref.WeakReference
import java.util.*

class MeViewModel: ViewModel() {

    private var mFragment: WeakReference<BaseFragment<*,*>>? = null

    val mLoginState = MutableLiveData<Boolean>()
    val mUserInfo = MutableLiveData<UserInfo>()
    val mAutoSignInEnable = MutableLiveData<Boolean>()
    val mSyncFavoriteEnable = MutableLiveData<Boolean>()

    init {
        mAutoSignInEnable.postValue(SharedPreferencesHelper
            .getUserSp()
            .getBoolean(SP_KEY_AUTO_SIGN_IN, false))
        mSyncFavoriteEnable.postValue(SharedPreferencesHelper
            .getUserSp()
            .getBoolean(SP_KEY_SYNC_FAVORITE, false))
    }

    fun scheduleJob(v: View) {
        val nextState = (mAutoSignInEnable.value != true)
        mAutoSignInEnable.postValue(nextState)
        SharedPreferencesHelper.getUserSp()
            .edit()
            .putBoolean(SP_KEY_AUTO_SIGN_IN, nextState)
            .apply()
        if (nextState) {
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
        } else {
            WorkManager.getInstance().cancelAllWorkByTag(SignInWorker::class.java.simpleName)
        }
        sendKVEvent(EVENT_AUTO_SIGN, Properties().apply {
            setProperty(PROP_IS_AUTO_SIGN_ENABLE, nextState.toString())
        })
    }

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
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val doc = Jsoup
                    .connect("$SERVER_BASE_URL$HOME_URL?mod=spacecp&ac=credit&showcredit=1")
                    .cookies(getCookies())
                    .ignoreHttpErrors(true)
                    .get()
                val userInfo = parseUserInfo(doc)
                // login failed
                if (userInfo.userName.isNullOrEmpty()) {
                    disableAutoSign()
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
                    UserData.loggedInState = false
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
    }

    @SuppressLint("ApplySharedPref")
    @WorkerThread
    private fun disableAutoSign() {
        WorkManager.getInstance().cancelAllWorkByTag(SignInWorker::class.java.simpleName)
        SharedPreferencesHelper.getUserSp().edit().putBoolean(SP_KEY_AUTO_SIGN_IN, false).commit()
        mAutoSignInEnable.postValue(false)
    }

    fun setSyncFavorite(v: View) {
        val nextState = (mSyncFavoriteEnable.value != true)
        mSyncFavoriteEnable.postValue(nextState)
        sendKVEvent(EVENT_SYNC_FAVORITE, Properties().apply {
            setProperty(
                PROP_IS_SYNC_FAVORITE_ENABLE,
                nextState.toString()
            )
        })
        SharedPreferencesHelper.getUserSp()
            .edit()
            .putBoolean(SP_KEY_SYNC_FAVORITE, nextState)
            .apply()
    }
}
