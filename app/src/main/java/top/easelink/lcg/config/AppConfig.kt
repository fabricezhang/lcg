package top.easelink.lcg.config

import com.tencent.stat.StatConfig

object AppConfig {

    private const val CONFIG_APP_RELEASE_URL = "app_release_url"

    fun getAppReleaseUrl(): String {
        return StatConfig.getCustomProperty(CONFIG_APP_RELEASE_URL, "thread-1073834-1-1.html")
    }

}