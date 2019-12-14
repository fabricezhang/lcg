package top.easelink.lcg.ui.main.model

data class ReplyPostEvent(val replyUrl: String, val author: String)

data class ScreenCaptureEvent(val imagePath: String)

data class OpenForumEvent(val title: String, val url: String)

data class OpenArticleEvent(val url: String)

object OpenNotificationsPageEvent

data class NewMessageEvent(val notificationInfo: NotificationInfo)