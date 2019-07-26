package top.easelink.lcg.ui.main.model;

import top.easelink.lcg.ui.main.source.model.Article;

public class OpenArticleEvent {

    private Article mArticle;

    public OpenArticleEvent(Article article) {
        mArticle = article;
    }

    public Article getArticle() {
        return mArticle;
    }
}