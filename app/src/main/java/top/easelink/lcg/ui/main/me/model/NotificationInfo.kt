package top.easelink.lcg.ui.main.me.model

data class NotificationInfo(val message: Int, val follower: Int, val posts: List<MyPostInfo>, val systemNotifications: List<SystemNotifications>)

data class MyPostInfo(val dateTime: String, val ntcBody: String, val collapseMsg: String)

data class SystemNotifications(val dateTime: String, val ntcBody: String, val quote: String)