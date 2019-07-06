package top.easelink.lcg.ui.main.model;

public class OpenArticleEvent {

    private Article mArticle;

    public OpenArticleEvent(Article article) {
        mArticle = article;
    }

    public Article getArticle() {
        return mArticle;
    }
}