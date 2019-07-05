package top.easelink.lcg.ui.home.source.remote;

import android.text.TextUtils;
import io.reactivex.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import timber.log.Timber;
import top.easelink.lcg.ui.home.model.Article;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * author : junzhang
 * date   : 2019-07-04 16:22
 * desc   :
 */
public class RxArticleService {

    private static final String BASE_URL = "https://www.52pojie.cn/forum.php?mod=guide&view=";
    private static final String HOT = "hot";
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
    public Observable<List<Article>> getArticles(@Nullable final Integer pageNum){
        return Observable.create(emitter -> {
            try {
                Document doc = Jsoup.connect(BASE_URL+HOT + "&page=" + pageNum).get();
                Elements elements = doc.select("tbody");
                List<Article> list = new ArrayList<>();
                String title, author, date;
                Integer view, reply;
                for (Element element : elements) {
                    try {
                        title = extractFrom(element, "th.common", ".xst");
                        author = extractFrom(element, "td.by", "a[href*=uid]");
                        date = extractFrom(element, "td.by", "span");
                        reply = Integer.valueOf(extractFrom(element, "td.num", "a.xi2"));
                        view = Integer.valueOf(extractFrom(element, "td.num", "em"));
                        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                            list.add(new Article(title, author, date, view, reply));
                        }
                    } catch (NumberFormatException nbe) {
                        Timber.i(nbe);
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
}
