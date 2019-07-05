package top.easelink.lcg.ui.main.articles.viewmodel;

import androidx.databinding.ObservableField;
import top.easelink.lcg.ui.main.articles.model.Article;

public class ArticleItemViewModel {

    private final ArticleItemViewModelListener mListener;

    public final ObservableField<String> title;
    public final ObservableField<String> author;
    public final ObservableField<String> date;
    public final ObservableField<String> replyAndView;
    public final ObservableField<String> origin;

    private final Article article;

    public ArticleItemViewModel(Article article, ArticleItemViewModelListener listener) {
        this.article = article;
        this.mListener = listener;
        title = new ObservableField<>(article.getTitle());
        author = new ObservableField<>(article.getAuthor());
        date = new ObservableField<>(article.getDate());
        int reply = article.getReply() != null? article.getReply() : 0;
        int view = article.getView() != null? article.getView() : 0;
        replyAndView = new ObservableField<>(reply + " / " + view);
        origin = new ObservableField<>(article.getOrigin());
    }

    public void onItemClick() {
        mListener.onItemClick(article.getUrl());
    }

    public interface ArticleItemViewModelListener {

        void onItemClick(String articleUrl);
    }
}
