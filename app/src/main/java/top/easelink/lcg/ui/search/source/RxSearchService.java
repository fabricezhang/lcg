package top.easelink.lcg.ui.search.source;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import timber.log.Timber;
import top.easelink.lcg.ui.search.model.SearchResult;
import top.easelink.lcg.ui.search.model.SearchResults;

/**
 * author : junzhang
 * date   : 2019-07-04 16:22
 * desc   :
 */
public class RxSearchService {

    private static RxSearchService mInstance;

    public static RxSearchService getInstance() {
        if (mInstance == null) {
            synchronized(RxSearchService.class) {
                mInstance = new RxSearchService();
            }
        }
        return mInstance;
    }

    private RxSearchService() {
        // should avoid to instantiating RxSearchService from outside
    }

    public Observable<SearchResults> doSearchRequest(@NonNull final String requestUrl){
        return Observable.create(emitter -> {
            try {
                Document doc = Jsoup.connect(requestUrl).get();
                String title, content, url;
                List<SearchResult> list = new ArrayList<>();
                Elements elements = doc.select("div.result");
                for (Element element : elements) {
                    try {
                        title = extractFrom(element, "h3.c-title", "a");
                        url = extractAttrFrom(element, "href", "h3.c-title", "a");
                        content = extractFrom(element, "div.c-abstract");
                        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(url)) {
                            list.add(new SearchResult(title, content, url));
                        }
                    } catch (NumberFormatException nbe) {
                        Timber.v(nbe);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
                SearchResults searchResults = new SearchResults(list);
                try {
                    String nextPageUrl = doc.select("a.pager-next-foot").attr("href");
                    searchResults.setNextPageUrl(nextPageUrl);
                } catch (Exception e) {
                    // mute
                    searchResults.setNextPageUrl(null);
                }
                emitter.onNext(searchResults);
            } catch (Exception e) {
                Timber.e(e);
                emitter.onError(e);
            } finally {
                emitter.onComplete();
            }
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
