package top.easelink.lcg.ui.main.model;

import java.util.List;

public class ArticleDetail {

    private List<Post> mPostList;
    private String mArticleTitle;
    private String mNextPageUrl;

    public ArticleDetail(String articleTitle, List<Post> postList, String nextPageUrl) {
        mArticleTitle = articleTitle;
        mPostList = postList;
        mNextPageUrl = nextPageUrl;
    }

    public List<Post> getPostList() {
        return mPostList;
    }

    public String getArticleTitle() {
        return mArticleTitle;
    }

    public String getNextPageUrl() {
        return mNextPageUrl;
    }
}
