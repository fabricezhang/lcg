package top.easelink.lcg.ui.main.model;

public class OpenArticleEvent {

    private String mUrl;

    public OpenArticleEvent(String url) {
        mUrl = url;
    }

    public String getUrl() {
        return mUrl;
    }
}