package top.easelink.lcg.ui.main.model;

import java.util.List;

public class ArticleDetail {

    private List<Post> mPostList;
    private String mArticleTitle;

    public ArticleDetail(String articleTitle, List<Post> postList) {
        mArticleTitle = articleTitle;
        mPostList = postList;
    }

    public List<Post> getPostList() {
        return mPostList;
    }

    public String getArticleTitle() {
        return mArticleTitle;
    }
}
