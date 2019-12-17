package top.easelink.lcg.ui.info

import top.easelink.lcg.ui.main.source.local.SP_KEY_LOGGED_IN
import top.easelink.lcg.ui.main.source.local.SP_KEY_USER_NAME
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper

object UserData {

    var username: String
        get() {
            return SharedPreferencesHelper.getUserSp().getString(SP_KEY_USER_NAME, "").orEmpty()
        }
        set(value) {
            SharedPreferencesHelper.getUserSp().edit().putString(SP_KEY_USER_NAME, value).apply()
        }

    var loggedInState = false
        set(value) {
            field = value
            SharedPreferencesHelper
                .getUserSp()
                .edit()
                .putBoolean(SP_KEY_LOGGED_IN, value)
                .apply()
        }

    init {
        loggedInState = SharedPreferencesHelper.getUserSp().getBoolean(SP_KEY_LOGGED_IN, false)
    }

}

