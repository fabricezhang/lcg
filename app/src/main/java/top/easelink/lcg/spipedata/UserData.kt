package top.easelink.lcg.spipedata

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import top.easelink.framework.threadpool.CommonPool
import top.easelink.lcg.ui.main.source.local.ArticlesLocalDataSource
import top.easelink.lcg.utils.SharedPreferencesHelper

object UserData {

    var avatar: String
        get() = get(SP_KEY_USER_AVATAR, "")
        set(value) = put(SP_KEY_USER_AVATAR, value)

    var group: String
        get() = get(SP_KEY_USER_GROUP, "")
        set(value) = put(SP_KEY_USER_GROUP, value)

    var coin: String
        get() = get(SP_KEY_USER_COIN, "")
        set(value) = put(SP_KEY_USER_COIN, value)

    var credit: String
        get() = get(SP_KEY_USER_CREDIT, "")
        set(value) = put(SP_KEY_USER_CREDIT, value)

    var username: String
        get() = get(SP_KEY_USER_NAME, "")
        set(value) = put(SP_KEY_USER_NAME, value)

    var signInState: String
        get() = get(SP_KEY_SIGN_IN_STATE, "")
        set(value) = put(SP_KEY_SIGN_IN_STATE, value)

    var loggedInState: Boolean
        get() = get(SP_KEY_LOGGED_IN, false)
        set(value) = put(SP_KEY_LOGGED_IN, value)

    fun clearAll() {
        SharedPreferencesHelper.getUserSp().edit().clear().apply()
        GlobalScope.launch(CommonPool) {
            ArticlesLocalDataSource.delAllArticlesFromFavorite()
        }
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun <T: Any> get(key: String, default: T): T {
        SharedPreferencesHelper.getUserSp().let {
            val res = when (default) {
                is String -> it.getString(key, default)
                is Int -> it.getInt(key, default)
                is Boolean -> it.getBoolean(key, default)
                else -> null
            }
            return res as? T?:default
        }
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun <T: Any?> put(key: String, value: T) {
        SharedPreferencesHelper
            .getUserSp()
            .edit()
            .apply {
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> return
                }
            }
            .apply()
    }

}

