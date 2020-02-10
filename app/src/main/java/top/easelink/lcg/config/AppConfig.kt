package top.easelink.lcg.config

import android.content.Context
import android.content.SharedPreferences
import com.tencent.stat.StatConfig
import top.easelink.lcg.LCGApp

object AppConfig {

    private const val CONFIG_SP = "config_sp"

    private const val CONFIG_APP_RELEASE_URL = "app_release_page"
    private const val CONFIG_ENABLE_FOLLOW_REDIRECTS = "follow_redirects"
    private const val CONFIG_SEARCH_OPEN_RESULT_IN_WEBVIEW = "open_result_in_webview"
    private const val CONFIG_DEFAULT_SEARCH_ENGINE = "default_search_engine"
    private const val CONFIG_AUTO_SIGN_IN = "auto_sign_in"
    private const val CONFIG_SYNC_FAVORITES = "sync_favorites"

    fun getAppReleaseUrl(): String {
        return StatConfig.getCustomProperty(CONFIG_APP_RELEASE_URL, "thread-1073834-1-1.html")
    }

    fun followRedirectsEnable(): Boolean {
        return "enable" == StatConfig.getCustomProperty(CONFIG_ENABLE_FOLLOW_REDIRECTS, "enable")
    }

    // open search result method, true -> WebView false -> try parse to native
    var searchResultShowInWebView: Boolean
        get() = get(CONFIG_SEARCH_OPEN_RESULT_IN_WEBVIEW, false)
        set(value) = put(CONFIG_SEARCH_OPEN_RESULT_IN_WEBVIEW, value)

    var defaultSearchEngine: Int
        get() = get(CONFIG_DEFAULT_SEARCH_ENGINE, 0)
        set(value) = put(CONFIG_DEFAULT_SEARCH_ENGINE, value)

    var autoSignEnable: Boolean
        get() = get(CONFIG_AUTO_SIGN_IN, true)
        set(value) = put(CONFIG_AUTO_SIGN_IN, value)

    var syncFavorites: Boolean
        get() = get(CONFIG_SYNC_FAVORITES, true)
        set(value) = put(CONFIG_SYNC_FAVORITES, value)


    private fun getConfigSp(): SharedPreferences {
        return LCGApp.getInstance().getSharedPreferences(CONFIG_SP, Context.MODE_PRIVATE)
    }

    @Suppress("UNCHECKED_CAST", "IMPLICIT_CAST_TO_ANY")
    private fun <T: Any> get(key: String, default: T): T {
        getConfigSp().let {
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
        getConfigSp()
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

