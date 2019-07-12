package top.easelink.lcg.ui.main.article.viewmodel;

import android.view.View;
import androidx.databinding.ObservableField;
import timber.log.Timber;
import top.easelink.lcg.ui.main.model.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostViewModel {

    public final ObservableField<String> url;
    public final ObservableField<String> author;
    public final ObservableField<String> date;

    private final Post post;

    PostViewModel(Post post) {
        this.post = post;
        url = new ObservableField<>(post.getAvatar());
        author = new ObservableField<>(post.getAuthor());
        date = new ObservableField<>(post.getDate());
    }

    public boolean onItemClick(View view) {
        String content = post.getContent();
        String patternStr = "https://www.lanzous.com/[a-zA-Z0-9]{4,10}";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        List<String> urls = new ArrayList<>();
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        for(String s: urls) {
            Timber.d(s);
        }
        return true;
    }
}
