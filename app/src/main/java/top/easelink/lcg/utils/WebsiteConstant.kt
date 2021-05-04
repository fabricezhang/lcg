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
    const val FORUM_BASE_QUERY = "forum.php?mod=guide&view="
    const val BAIDU_SEARCH_BASE_URL = "http://zhannei.baidu.com/cse/"
    const val SEARCH_QUERY = "http://zhannei.baidu.com/cse/site?q=%s&click=1&cc=52pojie.cn&s=&nsid="
    const val FREE_CHAT_QUERY = "forum-10-1.html"
    const val WELFARE_QUERY = "forum-66-1.html"
    const val MOB_SECURITY_QUERY = "forum-65-%d.html"
    const val SOFTWARE_QUERY = "forum-16-%d.html"

    // map all forum url to this template so that can add order parameter
    const val FORUM_URL_QUERY = "forum.php?mod=forumdisplay&fid=%s"

    const val ORIGINAL_RELEASE_QUERY = "forum-2-%d.html"
    const val PROGRAM_LANGUAGE_QUERY = "forum-24-%d.html"
    const val ANIMATION_RELEASE_QUERY = "forum-6-%d.html"
    const val REVERSE_RESOURCE_QUERY = "forum-4-%d.html"
    const val QA_QUERY = "forum-8-%d.html"
    const val VIRUS_ANALYSIS_QUERY = "forum-32-%d.html"
    const val VIRUS_RESCUE_QUERY = "forum-50-%d.html"
    const val VIRUS_SAMPLE_QUERY = "forum-40-%d.html"

    const val MY_ARTICLES_QUERY = "forum.php?mod=guide&view=my"
    const val GET_FAVORITE_QUERY = "home.php?mod=space&do=favorite&view=me"
    const val ADD_TO_FAVORITE_QUERY =
        "home.php?mod=spacecp&ac=favorite&type=thread&id=%s&formhash=%s&infloat=yes&handlekey=k_favorite&inajax=1&ajaxtarget=fwin_content_k_favorite"

    const val CHECK_RULE_URL =
        SERVER_BASE_URL + "forum.php?mod=ajax&action=checkpostrule&inajax=yes&ac=reply&infloat=yes&handlekey=reply"

    // notifications
    const val NOTIFICATION_HOME_QUERY = "home.php?mod=space&do=notice"
    const val SYSTEM_NOTIFICATION_QUERY = "home.php?mod=space&do=notice&view=system"
    const val MYPOST_NOTIFICATION_QUERY = "home.php?mod=space&do=notice&view=mypost"

    // private message
    const val PRIVATE_MESSAGE_QUERY = "home.php?mod=space&do=pm"

    // home
    const val HOME_QUERY = "home.php?mod=spacecp&ac=credit&showcredit=1"
    const val PROFILE_QUERY = "home.php?mod=space&do=profile&from=space"
    const val LOGIN_QUERY = "member.php?mod=logging&action=login"

    // follow
    const val UNFOLLOW_URL = "home.php?mod=spacecp&ac=follow&op=del&fuid=123456"
    const val FOLLOW_PAGE_URL = "home.php?mod=follow"
    const val FOLLOW_FEED_QUERY =
        "home.php?mod=spacecp&ac=follow&op=getfeed&page=%d&archiver=%d&inajax=1&viewtype=follow"
    const val FOLLOWING_USERS_QUERY = "home.php?mod=follow&do=following"
    const val FOLLOWER_USERS_QUERY = "home.php?mod=follow&do=follower"

    // rank
    const val RANK_QUERY = "misc.php?mod=ranklist&type=thread&view=%s&orderby=%s"

    // QQ
    const val QQ_LOGIN_URL = SERVER_BASE_URL + "connect.php?mod=login&op=init&referer=index.php&statfrom=login_simple"

}