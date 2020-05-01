package top.easelink.lcg.config

import android.content.Context
import android.content.SharedPreferences
import com.tencent.stat.StatConfig
import top.easelink.lcg.appinit.LCGApp
import top.easelink.lcg.spipedata.UserData

object AppConfig {

    private const val CONFIG_SP = "config_sp"

    private const val CONFIG_APP_RELEASE_URL = "app_release_page"
    private const val CONFIG_JRS_URL = "jrs_page"
    private const val CONFIG_ENABLE_FOLLOW_REDIRECTS = "follow_redirects"
    private const val CONFIG_SEARCH_OPEN_RESULT_IN_WEBVIEW = "open_result_in_webview"
    private const val CONFIG_ARTICLE_HANDLE_PRE_TAG = "handle_pre_tag_in_article"
    private const val CONFIG_ARTICLE_IN_WEBVIEW = "open_article_in_webview"
    private const val CONFIG_ARTICLE_SHOW_RECOMMEND_FLAG = "article_show_recommend_flag"
    private const val CONFIG_DEFAULT_SEARCH_ENGINE = "default_search_engine"
    private const val CONFIG_AUTO_SIGN_IN = "auto_sign_in"
    private const val CONFIG_SYNC_FAVORITES = "sync_favorites"


    const val CONFIG_SEARCH_ENGINE_BAIDU = 1
    const val CONFIG_SEARCH_ENGINE_WUAI = 0
    // Config from Remote
    fun getAppReleaseUrl(): String {
        return StatConfig.getCustomProperty(CONFIG_APP_RELEASE_URL, "thread-1073834-1-1.html")
    }

    fun followRedirectsEnable(): Boolean {
        return "enable" == StatConfig.getCustomProperty(CONFIG_ENABLE_FOLLOW_REDIRECTS, "enable")
    }

    fun getJrsUrl(): String {
        return StatConfig.getCustomProperty(CONFIG_JRS_URL, "http://www.jrskq.com/")
    }

    // open search result method, true -> WebView false -> try parse to native
    var searchResultShowInWebView: Boolean
        get() = get(CONFIG_SEARCH_OPEN_RESULT_IN_WEBVIEW, false)
        set(value) = put(CONFIG_SEARCH_OPEN_RESULT_IN_WEBVIEW, value)

    // open article, true -> WebView false -> render article by native
    var articleShowInWebView: Boolean
        get() = get(CONFIG_ARTICLE_IN_WEBVIEW, false)
        set(value) = put(CONFIG_ARTICLE_IN_WEBVIEW, value)

    var articleHandlePreTag: Boolean
        get() = get(CONFIG_ARTICLE_HANDLE_PRE_TAG, true)
        set(value) = put(CONFIG_ARTICLE_HANDLE_PRE_TAG, value)

    var articleShowRecommendFlag: Boolean
        get() = get(CONFIG_ARTICLE_SHOW_RECOMMEND_FLAG, true)
        set(value) = put(CONFIG_ARTICLE_SHOW_RECOMMEND_FLAG, value)

    var defaultSearchEngine: Int
        get() = get(
            CONFIG_DEFAULT_SEARCH_ENGINE,
            if (UserData.isLoggedIn) CONFIG_SEARCH_ENGINE_WUAI else CONFIG_SEARCH_ENGINE_BAIDU
        )
        set(value) = put(CONFIG_DEFAULT_SEARCH_ENGINE, value)

    var autoSignEnable: Boolean
        get() = get(CONFIG_AUTO_SIGN_IN, true)
        set(value) = put(CONFIG_AUTO_SIGN_IN, value)

    var syncFavorites: Boolean
        get() = get(CONFIG_SYNC_FAVORITES, true)
        set(value) = put(CONFIG_SYNC_FAVORITES, value)


    private fun getConfigSp(): SharedPreferences {
        return LCGApp.instance.getSharedPreferences(CONFIG_SP, Context.MODE_PRIVATE)
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

