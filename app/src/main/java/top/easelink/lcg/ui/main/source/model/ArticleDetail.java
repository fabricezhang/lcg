package top.easelink.lcg.ui.main.source.model;

import androidx.annotation.Nullable;

import java.util.List;

public class ArticleDetail {

    private List<Post> mPostList;
    private String mArticleTitle;
    private String mNextPageUrl;
    private ArticleAbstractResponse mArticleAbstractResponse;

    public ArticleDetail(String articleTitle,
                         List<Post> postList,
                         String nextPageUrl,
                         @Nullable ArticleAbstractResponse articleAbstractResponse) {
        mArticleTitle = articleTitle;
        mPostList = postList;
        mNextPageUrl = nextPageUrl;
        mArticleAbstractResponse = articleAbstractResponse;
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

    @Nullable
    public ArticleAbstractResponse getArticleAbstractResponse() {
        return mArticleAbstractResponse;
    }
}
