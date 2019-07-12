package top.easelink.lcg.ui.main.model.dto;

import top.easelink.lcg.ui.main.model.Article;

import java.util.List;

public class ForumPage {

    private List<Article> mArticleList;

    public ForumPage(List<Article> articleList) {
        mArticleList = articleList;
    }

    public List<Article> getArticleList() {
        return mArticleList;
    }
}
