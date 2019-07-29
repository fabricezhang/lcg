package top.easelink.lcg.ui.main.model;

public class OpenForumEvent {

    private String mUrl;
    private String mTitle;

    public OpenForumEvent(String title, String url) {
        mTitle = title;
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getTitle() {
        return mTitle;
    }
}