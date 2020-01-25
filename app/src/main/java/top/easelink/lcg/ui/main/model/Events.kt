package top.easelink.lcg.ui.main.model

data class ReplyPostEvent(val replyUrl: String, val author: String)

data class ScreenCaptureEvent(val imagePath: String)

data class OpenForumEvent(val title: String, val url: String, val showTab: Boolean)

data class OpenArticleEvent(val url: String)

data class NewMessageEvent(val notificationInfo: NotificationInfo)

data class OpenLargeImageViewEvent(val url: String)