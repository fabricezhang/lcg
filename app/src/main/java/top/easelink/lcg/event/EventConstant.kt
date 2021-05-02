package top.easelink.lcg.event

// Article
const val EVENT_OPEN_ARTICLE = "open_article"
const val EVENT_TAP_FOR_CODE = "tap_for_code" // 点击文章Code排版
const val EVENT_SHARE_ARTICLE_URL = "share_article_url" // 分享文章链接
const val EVENT_CAPTURE_ARTICLE = "capture_article" // 文章截图

// Profile
const val EVENT_OPEN_PROFILE = "open_profile" // 点击用户头像
const val EVENT_OPEN_PROFILE_PAGE = "open_profile_page" // 打开个人详情页
const val EVENT_SUBSCRIBE_USER = "subscribe_user" // 收听用户

// Forum
const val EVENT_OPEN_FORUM = "open_forum"
const val PROP_FORUM_NAME = "prop_forum_name"
const val CHANGE_THREAD = "change_thread"

// App
const val EVENT_AUTO_SIGN = "auto_sign"
const val PROP_IS_AUTO_SIGN_ENABLE = "is_auto_sign_enable"

// Favorite
const val EVENT_ADD_TO_FAVORITE = "add_to_favorite"
const val EVENT_SYNC_FAVORITE = "sync_favorite"
const val PROP_IS_SYNC_FAVORITE_ENABLE = "is_sync_favorite_enable"

// Follow
const val EVENT_OPEN_FOLLOW_PAGE = "open_follow_page"

// 单独Key的事件，为避免增加过多key，导致超出免费限额，使用Event中的key字段作为区分
const val EVENT_SINGLE_KEY_EVENT = "single_key_event"