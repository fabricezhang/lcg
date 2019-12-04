package top.easelink.lcg.ui.main.me.model

data class NotificationInfo(val message: Int, val follower: Int, val posts: Int, val systemNotifications: Int) {
    fun isEmpty(): Boolean {
        return message == 0 && follower == 0 && posts == 0 && systemNotifications == 0
    }
    fun isNotEmpty(): Boolean {
        return message > 0 || follower > 0 || posts > 0 || systemNotifications > 0
    }
}

data class MyPostInfo(val dateTime: String, val ntcBody: String, val collapseMsg: String)

data class SystemNotifications(val dateTime: String, val ntcBody: String, val quote: String)