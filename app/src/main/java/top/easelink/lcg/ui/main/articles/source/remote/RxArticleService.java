package top.easelink.lcg.ui.main.articles.source.remote;

import android.text.TextUtils;
import androidx.annotation.NonNull;
import io.reactivex.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import timber.log.Timber;
import top.easelink.lcg.ui.main.articles.model.Article;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * author : junzhang
 * date   : 2019-07-04 16:22
 * desc   :
 */
public class RxArticleService {

    public static final String SERVER_BASE_URL = "https://www.52pojie.cn/";
    private static final String FORUM_BASE_URL = "forum.php?mod=guide&view=";

    private static RxArticleService mInstance;

    public static RxArticleService getInstance() {
        if (mInstance == null) {
            synchronized(RxArticleService.class) {
                mInstance = new RxArticleService();
            }
        }
        return mInstance;
    }

    private RxArticleService() {

    }
    public Observable<List<Article>> getArticles(@NonNull final String param, @Nullable final Integer pageNum){
        return Observable.create(emitter -> {
            try {
                Document doc = Jsoup.connect(SERVER_BASE_URL + FORUM_BASE_URL + param + "&page=" + pageNum).get();
                Elements elements = doc.select("tbody");
                List<Article> list = new ArrayList<>();
                String title, author, date, url, origin;
                Integer view, reply;
                for (Element element : elements) {
                    try {
                        reply = Integer.valueOf(extractFrom(element, "td.num", "a.xi2"));
                        view = Integer.valueOf(extractFrom(element, "td.num", "em"));
                        title = extractFrom(element, "th.common", ".xst");
                        author = extractFrom(element, "td.by", "a[href*=uid]");
                        date = extractFrom(element, "td.by", "span");
                        url = extractAttrFrom(element, "href","th.common", "a.xst");
                        origin = extractFrom(element, "td.by", "a[target]");
                        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                            list.add(new Article(title, author, date, url, view, reply, origin));
                        }
                    } catch (NumberFormatException nbe) {
                        Timber.v(nbe);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                emitter.onNext(list);
            } catch (Exception e) {
                Timber.e(e);
                emitter.onError(e);
            }
            emitter.onComplete();
        });
    }

    private String extractFrom(Element element, String...tags) {
        if (tags == null || tags.length == 0) {
            return element == null? null : element.text();
        }
        Elements e = new Elements(element);
        for (String tag : tags) {
            e = e.select(tag);
            if (e.isEmpty()) {
                break;
            }
        }
        return e.text();
    }

    private String extractAttrFrom(Element element, String attr, String...tags) {
        if (tags == null || tags.length == 0) {
            return element == null? null : element.text();
        }
        Elements e = new Elements(element);
        for (String tag : tags) {
            e = e.select(tag);
            if (e.isEmpty()) {
                break;
            }
        }
        return e.attr(attr);
    }
}
