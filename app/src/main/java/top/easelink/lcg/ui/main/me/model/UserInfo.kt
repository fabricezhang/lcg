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

    override fun equals(other: Any?): Boolean {
        return (other is UserInfo
                && userName == other.userName
                && avatarUrl == other.avatarUrl
                && groupInfo == other.groupInfo
                && wuaiCoin == other.wuaiCoin
                && credit == other.credit
                && signInStateUrl == other.signInStateUrl)
    }

    override fun hashCode(): Int {
        var result = userName?.hashCode() ?: 0
        result = 31 * result + (avatarUrl?.hashCode() ?: 0)
        result = 31 * result + (groupInfo?.hashCode() ?: 0)
        result = 31 * result + (wuaiCoin?.hashCode() ?: 0)
        result = 31 * result + (credit?.hashCode() ?: 0)
        result = 31 * result + (signInStateUrl?.hashCode() ?: 0)
        result = 31 * result + (messageText?.hashCode() ?: 0)
        return result
    }

}