package top.easelink.lcg.utils

/**
 * author : junzhang
 * date   : 2019-07-11 17:24
 * desc   :
 */
object WebsiteConstant {
    const val URL_KEY = "URL_KEY"
    const val EXTRA_TABLE_HTML = "EXTRA_TABLE_HTML"
    const val SERVER_BASE_URL = "https://www.52pojie.cn/"
    const val FORUM_BASE_URL = "forum.php?mod=guide&view="
    const val BAIDU_SEARCH_BASE_URL = "http://zhannei.baidu.com/cse/"
    const val SEARCH_URL = "http://zhannei.baidu.com/cse/site?q=%s&click=1&cc=52pojie.cn&s=&nsid="
    const val FREE_CHAT_URL = "forum-10-1.html"
    const val WELFARE_URL = "forum-66-1.html"
    const val MOB_SECURITY_URL = "forum-65-%d.html"
    const val SOFTWARE_URL = "forum-16-%d.html"

    // map all forum url to this template so that can add order parameter
    const val FORUM_URL_TEMPLATE = "forum.php?mod=forumdisplay&fid=%s"

    const val ORIGINAL_RELEASE_URL = "forum-2-%d.html"
    const val PROGRAM_LANGUAGE_URL = "forum-24-%d.html"
    const val ANIMATION_RELEASE_URL = "forum-6-%d.html"
    const val REVERSE_RESOURCE_URL = "forum-4-%d.html"
    const val QA_URL = "forum-8-%d.html"
    const val VIRUS_ANALYSIS_URL = "forum-32-%d.html"
    const val VIRUS_RESCUE_URL = "forum-50-%d.html"
    const val VIRUS_SAMPLE_URL = "forum-40-%d.html"

    const val MY_ARTICLES_URL = "forum.php?mod=guide&view=my"
    const val GET_FAVORITE_QUERY = "home.php?mod=space&do=favorite&view=me"
    const val ADD_TO_FAVORITE_QUERY = "home.php?mod=spacecp&ac=favorite&type=thread&id=%s&formhash=%s&infloat=yes&handlekey=k_favorite&inajax=1&ajaxtarget=fwin_content_k_favorite"

    const val CHECK_RULE_URL = SERVER_BASE_URL + "forum.php?mod=ajax&action=checkpostrule&inajax=yes&ac=reply&infloat=yes&handlekey=reply"

    // notifications
    const val NOTIFICATION_HOME_URL = "home.php?mod=space&do=notice"
    const val SYSTEM_NOTIFICATION_URL = "home.php?mod=space&do=notice&view=system"
    const val MYPOST_NOTIFICATION_URL = "home.php?mod=space&do=notice&view=mypost"

    // private message
    const val PRIVATE_MESSAGE_URL = "home.php?mod=space&do=pm"
    // im
    const val IM_HOME_URL = "home.php?mod=space&do=pm"

    const val HOME_URL = "home.php"
    const val LOGIN_URL = "member.php?mod=logging&action=login"
    const val APP_RELEASE_PAGE = "thread-1073834-1-1.html"
}