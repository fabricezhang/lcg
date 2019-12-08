package top.easelink.lcg.ui.main.model

data class ReplyPostEvent(val replyUrl: String, val author: String)

data class OpenForumEvent(val title: String, val url: String)

data class OpenArticleEvent(val url: String)

object OpenNotificationsPageEvent

data class NewMessageEvent(val notificationInfo: NotificationInfo)

data class NotificationInfo(val message: Int, val follower: Int, val posts: Int, val systemNotifications: Int) {
    fun isEmpty(): Boolean {
        return !isNotEmpty()
    }
    fun isNotEmpty(): Boolean {
        return message > 0 || follower > 0 || posts > 0 || systemNotifications > 0
    }
}