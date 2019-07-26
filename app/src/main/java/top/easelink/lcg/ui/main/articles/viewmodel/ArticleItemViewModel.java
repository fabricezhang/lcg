package top.easelink.lcg.ui.main.articles.viewmodel;

import androidx.databinding.ObservableField;
import org.greenrobot.eventbus.EventBus;
import top.easelink.lcg.ui.main.source.model.Article;
import top.easelink.lcg.ui.main.model.OpenArticleEvent;

public class ArticleItemViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> author;
    public final ObservableField<String> date;
    public final ObservableField<String> replyAndView;
    public final ObservableField<String> origin;

    private final Article article;

    public ArticleItemViewModel(Article article) {
        this.article = article;
        title = new ObservableField<>(article.getTitle());
        author = new ObservableField<>(article.getAuthor());
        date = new ObservableField<>(article.getDate());
        int reply = article.getReply() != null? article.getReply() : 0;
        int view = article.getView() != null? article.getView() : 0;
        replyAndView = new ObservableField<>(reply + " / " + view);
        origin = new ObservableField<>(article.getOrigin());
    }

    public void onItemClick() {
        OpenArticleEvent event = new OpenArticleEvent(article);
        EventBus.getDefault().post(event);
    }
}
