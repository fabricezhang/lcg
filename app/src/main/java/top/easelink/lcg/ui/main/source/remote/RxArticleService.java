package top.easelink.lcg.ui.main.source.remote;

import android.text.TextUtils;
import android.util.ArrayMap;
import androidx.annotation.NonNull;
import io.reactivex.Observable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import timber.log.Timber;
import top.easelink.lcg.ui.main.model.Article;
import top.easelink.lcg.ui.main.model.Post;
import top.easelink.lcg.ui.main.model.dto.ArticleDetail;
import top.easelink.lcg.ui.main.model.BlockException;
import top.easelink.lcg.ui.main.model.dto.ForumPage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        // should avoid to instantiating RxArticleService from outside
    }

    private Observable<List<Article>> getArticles(@NonNull final String requestUrl){
        return Observable.create(emitter -> {
            try {
                Document doc = Jsoup.connect(requestUrl).get();
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
            } finally {
                emitter.onComplete();
            }
        });
    }

    public Observable<ForumPage> getForumArticles(@NonNull final String requestUrl){
        return Observable.create(emitter -> {
            Timber.d("52pojie " + requestUrl);
            try {
                Document doc = Jsoup.connect(SERVER_BASE_URL + requestUrl).get();
                Elements elements = doc.select("tbody[id^=normal]");
                if (elements.isEmpty()) {
                    String html = doc.html();
                    Timber.d(html);
                    elements = doc.select("tbody");
                }
                List<Article> list = new ArrayList<>();
                String title, author, date, url, origin;
                Integer view, reply;
                for (Element element : elements) {
                    try {
                        reply = Integer.valueOf(extractFrom(element, "td.num", "a.xi2"));
                        view = Integer.valueOf(extractFrom(element, "td.num", "em"));
                        title = extractFrom(element, "th.new", ".xst");
                        if (TextUtils.isEmpty(title)) {
                            title = extractFrom(element, "th.common", ".xst");
                        }
                        author = extractFrom(element, "td.by", "a[href*=uid]");
                        date = extractFrom(element, "td.by", "span");
                        url = extractAttrFrom(element, "href","th.new", "a.xst");
                        if (TextUtils.isEmpty(url)) {
                            url = extractAttrFrom(element, "href","th.common", "a.xst");
                        }
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
                ForumPage forumPage = new ForumPage(list);
                emitter.onNext(forumPage);
            } catch (Exception e) {
                Timber.e(e);
                emitter.onError(e);
            } finally {
                emitter.onComplete();
            }
        });
    }

    public Observable<List<Article>> getHomePageArticles(@NonNull final String param, @Nullable final Integer pageNum){
        String requestUrl = SERVER_BASE_URL + FORUM_BASE_URL + param + "&page=" + pageNum;
        return getArticles(requestUrl);
    }

    public Observable<ArticleDetail> getArticleDetail(@NonNull final String url){
        return Observable.create(emitter -> {
            try {
                Document doc = Jsoup.connect(SERVER_BASE_URL + url).get();
                Element titleElement = doc.selectFirst("span#thread_subject");
                String title;
                if (titleElement == null) {
                    emitter.onError(new BlockException());
                    return;
                } else {
                    title = titleElement.text();
                }
                Element nextPageElement = doc.selectFirst("a.nxt");
                String nextPageUrl;
                if (nextPageElement != null) {
                    nextPageUrl = nextPageElement.attr("href");
                } else {
                    nextPageUrl = "";
                }
                List<Map<String, String>> avatarsAndNames = getAvatarAndName(doc);
                List<String> contents = getContent(doc);
                List<String> datetimes = getDateTime(doc);
                List<Post> postList = new ArrayList<>(avatarsAndNames.size());
                for (int i = 0; i< avatarsAndNames.size(); i++) {
                    try {
                        Post post = new Post(avatarsAndNames.get(i).get("name"),
                                avatarsAndNames.get(i).get("avatar"),
                                datetimes.get(i),
                                contents.get(i));
                        postList.add(post);
                    } catch (NullPointerException npe) {
                        // will skip a loop if there's any npe occurs
                        Timber.e(npe);
                    }
                }
                ArticleDetail articleDetail = new ArticleDetail(title, postList, nextPageUrl);
                emitter.onNext(articleDetail);
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

    private List<Map<String, String>> getAvatarAndName(Document document) {
        List<Map<String, String>> list = new ArrayList<>(12);
        Elements elements = document.select("td[rowspan]");
        for (Element element: elements) {
            Map<String, String> avatarAndName = new ArrayMap<>(2);
            avatarAndName.put("avatar", element.select("div.avatar").select("img").attr("src"));
            avatarAndName.put("name", element.select("a.xw1").text());
            list.add(avatarAndName);
        }
        return list;
    }

    private List<String> getDateTime(Document document) {
        List<String> list = new ArrayList<>();
        for (Element element : document.select("div.authi").select("em")) {
            list.add(element.text());
        }
        return list;
    }

    private List<String> getContent(Document doc) {
        ArrayList<String> list = new ArrayList<>();
        Elements elements = doc.select("div.pcb");
        Element tmpElement;
        String content;
        for (Element e: elements) {
            tmpElement = e.selectFirst("td.t_f");
            if (tmpElement == null) {
                content = e.selectFirst("div.locked").html();
            } else {
                content = processContentElement(tmpElement);
            }
            list.add(content);
        }
        return list;
    }

    private String processContentElement(Element element) {
        // remove picture tips
        element.select("div.tip").remove();
        // remove user level info etc
        element.select("script").remove();
        // convert all code
        for(Element e: element.getElementsByTag("pre")) {
            e.tagName("div");
            e.html("<font color=\"#ff0000\">代码暂时无法显示</font>");
        }

        Elements imgElements = element.getElementsByTag("img");
        for (int i = 0; i < imgElements.size(); i++) {
            Element imgElement = imgElements.get(i);
            String src = imgElement.attr("src");
            if (src.contains("https://static.52pojie.cn/static/") && !src.contains("none")) {
                imgElement.remove();
            }
            String attr = imgElement.attr("file");
            if (!TextUtils.isEmpty(attr)) {
                imgElement.attr("src", attr);
            }
        }
        return element.html();
    }
}
