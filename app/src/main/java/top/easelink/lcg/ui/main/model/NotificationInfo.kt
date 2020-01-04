package top.easelink.lcg.ui.main.model

data class NotificationInfo(
    val message: Int,
    val follower: Int,
    val posts: Int,
    val systemNotifications: Int
) {
    fun isEmpty(): Boolean {
        return !isNotEmpty()
    }
    fun isNotEmpty(): Boolean {
        return message > 0 || follower > 0 || posts > 0 || systemNotifications > 0
    }
}

data class SystemNotification(
    val title: String,
    val comment: String? = null,
    val dateTime: String
)

class NotificationModel(
    val notifications: List<BaseNotification>,
    val nextPageUrl: String
)

data class BaseNotification(
    val avatar: String,
    val content: String,
    val dateTime: String
)

data class Conversation(
    val avatar: String?,
    val username: String,
    val lastMessage: String,
    val lastMessageDateTime: String,
    val totalMessage: String?,
    val replyUrl: String?
)