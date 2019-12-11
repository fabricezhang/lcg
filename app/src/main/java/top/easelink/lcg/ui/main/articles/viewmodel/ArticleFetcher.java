package top.easelink.lcg.ui.main.articles.viewmodel;

public interface ArticleFetcher {
    int FETCH_INIT = 0;
    int FETCH_MORE = 1;
    int FETCH_BY_THREAD = 2;
    void fetchArticles(int type);
}