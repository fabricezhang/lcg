package top.easelink.lcg.ui.main.article.view;

public interface ArticleNavigator {
    void handleError(Throwable t);
    void onAddToFavoriteFinished(boolean res);
    void scrollToTop();
}
