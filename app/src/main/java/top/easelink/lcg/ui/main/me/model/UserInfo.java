package top.easelink.lcg.ui.main.me.model;

public class UserInfo {

    private String userName;
    private String avatarUrl;
    private String groupInfo;
    private String wuaiCoin;
    private String credit;
    private String signInStateUrl;
    private String messageText;

    public UserInfo(String userName,
                    String avatarUrl,
                    String groupInfo,
                    String wuaiCoin,
                    String credit,
                    String signInStateUrl) {
        this.avatarUrl = avatarUrl;
        this.groupInfo = groupInfo;
        this.userName = userName;
        this.wuaiCoin = wuaiCoin;
        this.credit = credit;
        this.signInStateUrl = signInStateUrl;
    }

    public UserInfo(String messageText) {
        this.messageText = messageText;
    }

    public String getUserName() {
        return userName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getGroupInfo() {
        return groupInfo;
    }

    public String getWuaiCoin() {
        return wuaiCoin;
    }

    public String getCredit() {
        return credit;
    }

    public String getSignInStateUrl() {
        return signInStateUrl;
    }

    public String getMessageText() {
        return messageText;
    }
}
