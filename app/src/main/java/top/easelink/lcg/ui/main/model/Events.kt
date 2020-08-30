package top.easelink.lcg.ui.main.model

class ReplyPostEvent(val replyUrl: String, val author: String)

class ScreenCaptureEvent(val imagePath: String)

class OpenForumEvent(val title: String, val url: String, val showTab: Boolean)

class OpenArticleEvent(val url: String)

class NewMessageEvent(val notificationInfo: NotificationInfo)

class OpenLargeImageViewEvent(val url: String)

class OpenHalfWebViewFragmentEvent(val html: String)