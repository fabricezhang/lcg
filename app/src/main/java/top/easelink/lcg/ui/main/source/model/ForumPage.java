package top.easelink.lcg.ui.main.source.model;

import java.util.List;

public class ForumPage {

    private List<Article> mArticleList;

    private List<ForumThread> mThreadList;

    public ForumPage(List<Article> articleList, List<ForumThread> threadList) {
        mArticleList = articleList;
        mThreadList = threadList;
    }

    public List<Article> getArticleList() {
        return mArticleList;
    }

    public List<ForumThread> getThreadList() {
        return mThreadList;
    }
}
