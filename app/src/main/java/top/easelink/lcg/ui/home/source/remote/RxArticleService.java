package top.easelink.lcg.ui.home.source.remote;

import android.text.TextUtils;
import io.reactivex.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import timber.log.Timber;
import top.easelink.lcg.ui.home.model.Article;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public Observable<List<Article>> getArticles(){
        return Observable.create(emitter -> {
            try {
                Document doc = Jsoup.connect(BASE_URL+HOT).get();
                Elements elements = doc.select("tbody");
                List<Article> list = new ArrayList<>();
                String title, author;
                for (Element element : elements) {
                    title = element
                            .select("th.common")
                            .select(".xst")
                            .text();
                    author = element
                            .select("td.by")
                            .select("a[c]")
                            .text();
                    if (title != null && author != null
                            && !TextUtils.isEmpty(title)
                            && !TextUtils.isEmpty(author)){
                        list.add(new Article(title, author));
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

    private List<Article> fake() {
        List<Article> articleList = new ArrayList<>();
        Random random = new Random();
        articleList.add(new Article("Article" + random.nextInt(),  "0"));
        articleList.add(new Article("Article" + random.nextInt(),  "1"));
        articleList.add(new Article("Article" + random.nextInt(),  "2"));
        articleList.add(new Article("Article" + random.nextInt(),  "3"));
        articleList.add(new Article("Article" + random.nextInt(),  "4"));
        articleList.add(new Article("Article" + random.nextInt(),  "5"));
        return articleList;
    }

}
