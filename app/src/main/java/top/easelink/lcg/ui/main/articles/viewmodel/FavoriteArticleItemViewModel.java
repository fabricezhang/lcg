package top.easelink.lcg.ui.main.articles.viewmodel;

import androidx.databinding.ObservableField;
import top.easelink.lcg.ui.main.source.model.ArticleEntity;

public class FavoriteArticleItemViewModel {

    public final ObservableField<String> title;
    public final ObservableField<String> author;
    public final ObservableField<String> content;


    private final ArticleEntity articleEntity;

    FavoriteArticleItemViewModel(ArticleEntity articleEntity) {
        this.articleEntity = articleEntity;
        title = new ObservableField<>(articleEntity.getTitle());
        author = new ObservableField<>(articleEntity.getAuthor());
        content = new ObservableField<>(articleEntity.getContent());
    }

    public void onItemClick() {
//        OpenArticleEvent event = new OpenArticleEvent(article);
//        EventBus.getDefault().post(event);
    }
}
