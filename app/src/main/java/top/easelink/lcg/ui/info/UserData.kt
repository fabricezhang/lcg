package top.easelink.lcg.ui.info

import top.easelink.lcg.ui.main.source.local.SPConstants.SP_KEY_LOGGED_IN
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper

object UserData {

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

