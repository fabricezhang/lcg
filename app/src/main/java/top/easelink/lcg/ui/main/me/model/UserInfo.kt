package top.easelink.lcg.ui.main.me.model

class UserInfo {
    var userName: String? = null
        private set
    var avatarUrl: String? = null
        private set
    var groupInfo: String? = null
        private set
    var wuaiCoin: String? = null
        private set
    var credit: String? = null
        private set
    var signInStateUrl: String? = null
        private set
    var messageText: String? = null
        private set

    constructor(
        userName: String?,
        avatarUrl: String?,
        groupInfo: String?,
        wuaiCoin: String?,
        credit: String?,
        signInStateUrl: String?
    ) {
        this.avatarUrl = avatarUrl
        this.groupInfo = groupInfo
        this.userName = userName
        this.wuaiCoin = wuaiCoin
        this.credit = credit
        this.signInStateUrl = signInStateUrl
    }

    constructor(messageText: String?) {
        this.messageText = messageText
    }

}