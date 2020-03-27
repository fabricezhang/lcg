package top.easelink.lcg.ui.main.follow.model

data class FollowInfo(
    val username: String,
    val avatar: String,
    val followOrUnFollowUrl: String,
    val followingNum: Int = 0,
    val followerNum: Int = 0,
    val lastAction: String = ""
)