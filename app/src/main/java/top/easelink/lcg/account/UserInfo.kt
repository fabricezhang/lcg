package top.easelink.lcg.account

class UserInfo(
    userName: String?,
    groupInfo: String? = null,
    avatarUrl: String? = null,
    wuaiCoin: String? = null,
    credit: String? = null,
    answerRate: String? = null,
    enthusiasticValue: String? = null,
    signInStateUrl: String? = null
) {

    var userName: String? = userName
        private set
    var avatarUrl: String? = avatarUrl
        private set
    var groupInfo: String? = groupInfo
        private set
    var wuaiCoin: String? = wuaiCoin
        private set
    var credit: String? = credit
        private set
    var answerRate: String? = answerRate
        private set
    var enthusiasticValue: String? = enthusiasticValue
        private set
    var signInStateUrl: String? = signInStateUrl
        private set

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
        return result
    }

    companion object {

        fun emptyUserInfo(): UserInfo {
            return UserInfo(userName = null)
        }
    }

}