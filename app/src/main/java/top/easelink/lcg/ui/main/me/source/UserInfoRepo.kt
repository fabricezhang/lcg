package top.easelink.lcg.ui.main.me.source

import top.easelink.lcg.network.JsoupClient
import top.easelink.lcg.account.UserInfo
import top.easelink.lcg.ui.main.source.parseUserInfo
import top.easelink.lcg.utils.WebsiteConstant

object UserInfoRepo {

    fun requestUserInfo(): UserInfo? = runCatching {
        JsoupClient
            .sendGetRequestWithQuery(WebsiteConstant.PROFILE_QUERY)
            .let {
                parseUserInfo(it)
            }
    }.getOrNull()

}