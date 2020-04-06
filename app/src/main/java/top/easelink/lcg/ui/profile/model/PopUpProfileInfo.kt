package top.easelink.lcg.ui.profile.model

data class PopUpProfileInfo(
    val imageX: Int,
    val imageY: Int,
    val imageUrl: String,
    val userName: String,
    val extraUserInfo: String?,
    val followInfo: Pair<String, String>?, // title -> url
    val profileUrl: String?
)