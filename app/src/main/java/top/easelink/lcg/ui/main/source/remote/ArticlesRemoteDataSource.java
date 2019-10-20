package top.easelink.lcg.ui.main.source.remote;

import android.text.TextUtils;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import timber.log.Timber;
import top.easelink.lcg.ui.main.model.BlockException;
import top.easelink.lcg.ui.main.source.ArticlesDataSource;
import top.easelink.lcg.ui.main.source.model.Article;
import top.easelink.lcg.ui.main.source.model.ArticleAbstractResponse;
import top.easelink.lcg.ui.main.source.model.ArticleDetail;
import top.easelink.lcg.ui.main.source.model.ForumPage;
import top.easelink.lcg.ui.main.source.model.ForumThread;
import top.easelink.lcg.ui.main.source.model.Post;

import static top.easelink.lcg.utils.CookieUtilsKt.getCookies;
import static top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL;

/**
 * author : junzhang
 * date   : 2019-07-04 16:22
 * desc   :
 */
public class ArticlesRemoteDataSource implements ArticlesDataSource {

    private static final String FORUM_BASE_URL = "forum.php?mod=guide&view=";

    private static ArticlesRemoteDataSource mInstance;

    public static ArticlesRemoteDataSource getInstance() {
        if (mInstance == null) {
            synchronized(ArticlesRemoteDataSource.class) {
                mInstance = new ArticlesRemoteDataSource();
            }
        }
        return mInstance;
    }

    private Gson gson;

    private ArticlesRemoteDataSource() {
        // should avoid to instantiating RxSearchService from outside
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }

    @Override
    public Observable<ForumPage> getForumArticles(@NonNull final String requestUrl){
        return Observable.create(emitter -> {
            String url = SERVER_BASE_URL + requestUrl;
            Document doc = Jsoup.connect(url).cookies(getCookies()).get();
            forumArticlesDocumentProcessor(doc, emitter);
        });
    }

    @Override
    public Observable<List<Article>> getHomePageArticles(@NonNull final String param, @Nullable final Integer pageNum){
        String requestUrl = SERVER_BASE_URL + FORUM_BASE_URL + param + "&page=" + pageNum;
        return getArticles(requestUrl);
    }

    @Override
    public Observable<ArticleDetail> getArticleDetail(@NonNull final String url){
        return Observable.create(emitter -> {
            try {
                Connection connection = Jsoup
                        .connect(SERVER_BASE_URL + url)
                        .cookies(getCookies())
                        .method(Connection.Method.GET);
                Connection.Response response = connection.execute();
                Document doc = response.parse();
                Element baiduJsonElement = doc.selectFirst("script");
                ArticleAbstractResponse articleAbstract = null;
                if (baiduJsonElement != null) {
                    try {
                        String json = baiduJsonElement.html().trim();
                        json = json.replaceAll("\u00a0", "");
                        articleAbstract = gson.fromJson(json, ArticleAbstractResponse.class);
                    } catch (Exception e) {
                        Timber.w(e);
                    }
                }
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
                        Post post = new Post(Objects.requireNonNull(avatarsAndNames.get(i).get("name")),
                                Objects.requireNonNull(avatarsAndNames.get(i).get("avatar")),
                                datetimes.get(i),
                                contents.get(i));
                        postList.add(post);
                    } catch (NullPointerException npe) {
                        // will skip a loop if there's any npe occurs
                        Timber.e(npe);
                    }
                }
                ArticleDetail articleDetail = new ArticleDetail(title, postList, nextPageUrl, articleAbstract);
                emitter.onNext(articleDetail);
            } catch (Exception e) {
                Timber.e(e);
                emitter.onError(e);
            } finally {
                emitter.onComplete();
            }
        });
    }

    private Observable<List<Article>> getArticles(@NonNull final String requestUrl){
        return Observable.create(emitter -> {
            try {
                Document doc = Jsoup.connect(requestUrl).cookies(getCookies()).get();
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

    private void forumArticlesDocumentProcessor(Document doc, ObservableEmitter<ForumPage> emitter) {
        try {
            Elements elements = doc.select("tbody[id^=normal]");
            if (elements.isEmpty()) {
                String htmls = doc.html();
                Timber.d(htmls);
                elements = doc.select("tbody");
            }
            List<Article> articleList = new ArrayList<>();
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
                        articleList.add(new Article(title, author, date, url, view, reply, origin));
                    }
                } catch (NumberFormatException nbe) {
                    Timber.v(nbe);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
            // for thread part
            elements = doc.getElementById("thread_types").getElementsByTag("li");
            List<ForumThread> threadList = new ArrayList<>();
            for (Element element: elements) {
                try {
                    String threadUrl = element.getElementsByTag("a").attr("href");
                    String name = element.getElementsByTag("font").first().text();
                    if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(threadUrl)) {
                        threadList.add(new ForumThread(name, threadUrl));
                    }
                } catch (Exception e) {
                    // don't care
                }
            }

            ForumPage forumPage = new ForumPage(articleList, threadList);
            emitter.onNext(forumPage);
        } catch (Exception e) {
            Timber.e(e);
            emitter.onError(e);
        } finally {
            emitter.onComplete();
        }
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

    @NonNull
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

    @NonNull
    private List<String> getDateTime(Document document) {
        List<String> list = new ArrayList<>();
        for (Element element : document.select("div.authi").select("em")) {
            list.add(element.text());
        }
        return list;
    }

    @NonNull
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
            e.siblingElements().remove();
            String s = e.html().replaceAll("\r\n", "<br/>").replaceAll(" ", "&nbsp;");
            e.html(s);
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
