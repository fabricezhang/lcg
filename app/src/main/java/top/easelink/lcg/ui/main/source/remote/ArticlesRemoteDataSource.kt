package top.easelink.lcg.ui.main.source.remote

import android.text.TextUtils
import android.util.ArrayMap
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import timber.log.Timber
import top.easelink.lcg.network.Client
import top.easelink.lcg.ui.main.model.BlockException
import top.easelink.lcg.ui.main.model.LoginRequiredException
import top.easelink.lcg.ui.main.source.ArticlesDataSource
import top.easelink.lcg.ui.main.source.FavoritesRemoteDataSource
import top.easelink.lcg.ui.main.source.checkMessages
import top.easelink.lcg.ui.main.source.model.*
import top.easelink.lcg.utils.WebsiteConstant
import top.easelink.lcg.utils.getCookies
import java.util.*

/**
 * author : junzhang
 * date   : 2019-07-04 16:22
 * desc   :
 */
object ArticlesRemoteDataSource: ArticlesDataSource, FavoritesRemoteDataSource {
    private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    override fun getForumArticles(
        query: String,
        processThreadList: Boolean
    ): ForumPage? {
        val doc = Client.sendRequestWithQuery(query)
        return processForumArticlesDocument(doc, processThreadList)
    }

    override fun getHomePageArticles(
        param: String,
        pageNum: Int
    ): List<Article> {
        val requestUrl =
            WebsiteConstant.SERVER_BASE_URL + WebsiteConstant.FORUM_BASE_URL + param + "&page=" + pageNum
        return getArticles(requestUrl)
    }

    override fun getArticleDetail(url: String): Observable<ArticleDetail> {
        return Observable.create { emitter: ObservableEmitter<ArticleDetail> ->
            try {
                val connection = Jsoup
                    .connect(WebsiteConstant.SERVER_BASE_URL + url)
                    .cookies(getCookies())
                    .method(Connection.Method.GET)
                val response = connection.execute()
                val doc = response.parse()
                checkMessages(doc)
                val baiduJsonElement = doc.selectFirst("script")
                var articleAbstract: ArticleAbstractResponse? = null
                if (baiduJsonElement != null) {
                    try {
                        var json = baiduJsonElement.html().trim { it <= ' ' }
                        json = json.replace("\u00a0".toRegex(), "")
                        articleAbstract = gson.fromJson(
                            json,
                            ArticleAbstractResponse::class.java
                        )
                    } catch (e: Exception) { // no need to handle
                    }
                }
                val titleElement = doc.selectFirst("span#thread_subject")
                val title: String
                title = if (titleElement == null) {
                    emitter.onError(BlockException())
                    return@create
                } else {
                    titleElement.text()
                }
                val nextPageElement = doc.selectFirst("a.nxt")
                val nextPageUrl: String
                nextPageUrl = if (nextPageElement != null) {
                    nextPageElement.attr("href")
                } else {
                    ""
                }
                val replyAddUrls = getReplyAddUrl(doc)
                val avatarsAndNames =
                    getAvatarAndName(doc)
                val contents = getContent(doc)
                val dateTimes = getDateTime(doc)
                val replyUrls = getReplyUrls(doc)
                val postList: MutableList<Post> =
                    ArrayList(avatarsAndNames.size)
                for (i in avatarsAndNames.indices) {
                    try {
                        val replyAddUrl: String? = if (i >= replyAddUrls.size) {
                            null
                        } else {
                            replyAddUrls[i]
                        }
                        val post = Post(
                            Objects.requireNonNull(
                                avatarsAndNames[i]["name"]
                            )!!,
                            Objects.requireNonNull(
                                avatarsAndNames[i]["avatar"]
                            )!!,
                            dateTimes[i],
                            contents[i],
                            replyUrls[i],
                            replyAddUrl
                        )
                        postList.add(post)
                    } catch (npe: NullPointerException) { // will skip a loop if there's any npe occurs
                        Timber.v(npe)
                    }
                }
                val fromHash = doc.selectFirst("input[name=formhash]").attr("value")
                val articleDetail =
                    ArticleDetail(title, postList, nextPageUrl, fromHash, articleAbstract)
                emitter.onNext(articleDetail)
            } catch (e: Exception) {
                Timber.e(e)
                emitter.onError(e)
            } finally {
                emitter.onComplete()
            }
        }
    }

    private fun getArticles(requestUrl: String): List<Article> {
        val list: MutableList<Article> = ArrayList()
        try {
            val doc = Client.sendRequestWithUrl(requestUrl)
            val elements = doc.select("tbody")
            var title: String?
            var author: String?
            var date: String?
            var url: String?
            var origin: String?
            var view: Int
            var reply: Int
            for (element in elements) {
                try {
                    reply = extractFrom(element, "td.num", "a.xi2")?.toInt()?:0
                    view = extractFrom(element, "td.num", "em")?.toInt()?:0
                    title = extractFrom(element, "th.common", ".xst")
                    author = extractFrom(element, "td.by", "a[href*=uid]")
                    date = extractFrom(element, "td.by", "span")
                    url = extractAttrFrom(element, "href", "th.common", "a.xst")
                    origin = extractFrom(element, "td.by", "a[target]")
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                        list.add(Article(title!!, author!!, date!!, url!!, view, reply, origin))
                    }
                } catch (nbe: NumberFormatException) {
                    Timber.v(nbe)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            return list
        }
    }

    private fun processForumArticlesDocument(
        doc: Document,
        processThreadList: Boolean
    ): ForumPage? {
        try {
            var elements = doc.select("tbody[id^=normal]")
            if (elements.isEmpty()) {
                val element = doc.getElementById("messagelogin")
                if (element != null) {
                    throw LoginRequiredException()
                }
            }
            val articleList: MutableList<Article> =
                ArrayList()
            var title: String?
            var author: String?
            var date: String?
            var url: String?
            var origin: String?
            var view: Int
            var reply: Int
            for (element in elements) {
                try {
                    reply = extractFrom(element, "td.num", "a.xi2")?.toInt()?:0
                    view = extractFrom(element, "td.num", "em")?.toInt()?:0
                    title = extractFrom(element, "th.new", ".xst")
                    if (TextUtils.isEmpty(title)) {
                        title = extractFrom(element, "th.common", ".xst")
                    }
                    author = extractFrom(element, "td.by", "a[href*=uid]")
                    date = extractFrom(element, "td.by", "span")
                    url = extractAttrFrom(element, "href", "th.new", "a.xst")
                    if (TextUtils.isEmpty(url)) {
                        url = extractAttrFrom(element, "href", "th.common", "a.xst")
                    }
                    origin = extractFrom(element, "td.by", "a[target]")
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                        articleList.add(
                            Article(
                                title!!,
                                author!!,
                                date!!,
                                url!!,
                                view,
                                reply,
                                origin
                            )
                        )
                    }
                } catch (nbe: NumberFormatException) {
                    Timber.v(nbe)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
            // for thread part
            val threadList: MutableList<ForumThread> =
                ArrayList()
            if (processThreadList) {
                val threadTypes = doc.getElementById("thread_types")
                if (threadTypes != null) {
                    for (elementByTag in threadTypes.getElementsByTag("li")) {
                        try {
                            val element = elementByTag.getElementsByTag("a").first()
                            elements = element.getElementsByTag("span")
                            if (elements.size > 0) {
                                elements.remove()
                            }
                            val threadUrl = element.attr("href")
                            val name = element.text().trim { it <= ' ' }
                            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(threadUrl)) {
                                threadList.add(ForumThread(name, threadUrl))
                            }
                        } catch (e: Exception) { // don't care
                        }
                    }
                }
            }
            return ForumPage(articleList, threadList);
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }

    private fun extractFrom(
        element: Element?,
        vararg tags: String
    ): String? {
        if (tags.isNullOrEmpty()) {
            return element?.text()
        }
        var e = Elements(element)
        for (tag in tags) {
            e = e.select(tag)
            if (e.isEmpty()) {
                break
            }
        }
        return e.text()
    }

    private fun extractAttrFrom(
        element: Element?,
        attr: String,
        vararg tags: String
    ): String? {
        if (tags.isNullOrEmpty()) {
            return element?.text()
        }
        var e = Elements(element)
        for (tag in tags) {
            e = e.select(tag)
            if (e.isEmpty()) {
                break
            }
        }
        return e.attr(attr)
    }

    private fun getReplyAddUrl(document: Document): List<String> {
        val list: MutableList<String> =
            ArrayList(12)
        try {
            val e = document.getElementById("recommend_add")
            list.add(e.attr("href"))
            val elements = document.select("a.replyadd")
            for (element in elements) {
                list.add(element.attr("href"))
            }
        } catch (npe: NullPointerException) { // no need to handle
        }
        return list
    }

    private fun getAvatarAndName(document: Document): List<Map<String, String>> {
        val list: MutableList<Map<String, String>> =
            ArrayList(12)
        val elements = document.select("td[rowspan]")
        for (element in elements) {
            val avatarAndName: MutableMap<String, String> =
                ArrayMap(2)
            avatarAndName["avatar"] = element.select("div.avatar").select("img").attr("src")
            avatarAndName["name"] = element.select("a.xw1").text()
            list.add(avatarAndName)
        }
        return list
    }

    private fun getDateTime(document: Document): List<String> {
        val list: MutableList<String> =
            ArrayList()
        for (element in document.select("div.authi").select("em")) {
            list.add(element.text())
        }
        return list
    }

    private fun getContent(doc: Document): List<String> {
        return doc.select("div.pcb")
            .filterNotNull()
            .mapTo(ArrayList<String>(), {
                val tmpElement = it.selectFirst("td.t_f")
                tmpElement?.let {
                    processContentElement(it)
                } ?:
                it.selectFirst("div.locked").html()
            })
    }

    private fun getReplyUrls(doc: Document): List<String> {
        val elements = doc.getElementsByClass("fastre")
        val list: MutableList<String> =
            ArrayList()
        for (e in elements) {
            list.add(e.attr("href"))
        }
        return list
    }

    private fun processContentElement(element: Element): String { // remove picture tips
        element.select("div.tip").remove()
        // remove user level info etc
        element.select("script").remove()
        // convert all code
        for (e in element.getElementsByTag("pre")) {
            e.siblingElements().remove()
            val s =
                e.html().replace("\r\n".toRegex(), "<br/>").replace(" ".toRegex(), "&nbsp;")
            e.html(s)
        }
        val imgElements = element.getElementsByTag("img")
        for (i in imgElements.indices) {
            val imgElement = imgElements[i]
            val src = imgElement.attr("src")
            if (src.contains("https://static.52pojie.cn/static/") && !src.contains("none")) {
                imgElement.remove()
            }
            val attr = imgElement.attr("file")
            if (!TextUtils.isEmpty(attr)) {
                imgElement.attr("src", attr)
            }
        }
        return element.html()
    }

    override fun addFavorites(
        threadId: String,
        formHash: String
    ): Observable<Boolean> {
        return Observable.fromCallable {
            try {
                Jsoup.connect(
                    String.format(
                        WebsiteConstant.FAVORITE_URL,
                        threadId,
                        formHash
                    )
                ).cookies(getCookies()).get()
                return@fromCallable true
            } catch (e: Exception) {
                Timber.e(e)
                return@fromCallable false
            }
        }
    }

    /**
     * Support user's post
     * @param query post url without base_server_url
     * @return status message
     */
    fun replyAdd(query: String): Observable<String> {
        return Observable.fromCallable {
            try {
                val doc = Client.sendRequestWithQuery(query)
                val message = doc.getElementsByClass("nfl").first().text()
                Timber.d(doc.html())
                Timber.d(message)
                return@fromCallable message
            } catch (e: Exception) {
                Timber.e(e)
                return@fromCallable "Error"
            }
        }
    }
}