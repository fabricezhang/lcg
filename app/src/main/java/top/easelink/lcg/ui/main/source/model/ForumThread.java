package top.easelink.lcg.ui.main.source.model;

public class ForumThread {
    private String threadName;
    private String threadUrl;

    public ForumThread(String threadName, String threadUrl) {
        this.threadName = threadName;
        this.threadUrl = threadUrl;
    }
    public String getThreadName() {
        return threadName;
    }

    public String getThreadUrl() {
        return threadUrl;
    }
}