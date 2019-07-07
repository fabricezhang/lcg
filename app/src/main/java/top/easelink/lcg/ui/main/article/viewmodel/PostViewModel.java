package top.easelink.lcg.ui.main.article.viewmodel;

import androidx.databinding.ObservableField;
import top.easelink.lcg.ui.main.model.Post;

public class PostViewModel {

    public final ObservableField<String> url;
    public final ObservableField<String> author;
    public final ObservableField<String> date;

    private final Post post;

    public PostViewModel(Post post) {
        this.post = post;
        url = new ObservableField<>(post.getAvatar());
        author = new ObservableField<>(post.getAuthor());
        date = new ObservableField<>(post.getDate());
    }

    public void onItemClick() {

    }
}
