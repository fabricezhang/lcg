package top.easelink.lcg.config

import com.tencent.stat.StatConfig

object AppConfig {

    private const val CONFIG_APP_RELEASE_URL = "app_release_page"
    private const val CONFIG_ENABLE_FOLLOW_REDIRECTS = "follow_redirects"

    fun getAppReleaseUrl(): String {
        return StatConfig.getCustomProperty(CONFIG_APP_RELEASE_URL, "thread-1073834-1-1.html")
    }

    fun followRedirectsEnable(): Boolean {
        return "enable" == StatConfig.getCustomProperty(CONFIG_ENABLE_FOLLOW_REDIRECTS, "enable")
    }

}