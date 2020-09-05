package top.easelink.lcg.account

import androidx.lifecycle.MutableLiveData

/**
 * Responsible for manage all account changes, ex login/logout/sign
 */
object AccountManager {

    val isLoggedIn = MutableLiveData(UserDataRepo.isLoggedIn)

    val isSignedToday = MutableLiveData(UserDataRepo.signInState.isNotEmpty())

    val userInfo = MutableLiveData<UserInfo>()

}