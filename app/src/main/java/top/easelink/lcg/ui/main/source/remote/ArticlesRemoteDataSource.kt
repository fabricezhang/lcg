package top.easelink.lcg.ui.main.source.remote

import android.text.TextUtils
import android.util.ArrayMap
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.HttpStatusException
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import timber.log.Timber
import top.easelink.framework.threadpool.BackGroundPool
import top.easelink.lcg.config.AppConfig
import top.easelink.lcg.network.JsoupClient
import top.easelink.lcg.ui.main.model.BlockException
import top.easelink.lcg.ui.main.model.LoginRequiredException
import top.easelink.lcg.ui.main.model.NetworkException
import top.easelink.lcg.ui.main.source.ArticlesDataSource
import top.easelink.lcg.ui.main.source.FavoritesRemoteDataSource
import top.easelink.lcg.ui.main.source.local.ArticlesDatabase
import top.easelink.lcg.ui.main.source.model.*
import top.easelink.lcg.utils.WebsiteConstant.ADD_TO_FAVORITE_QUERY
import top.easelink.lcg.utils.WebsiteConstant.FORUM_BASE_QUERY
import java.net.SocketTimeoutException
import java.util.*

/**
 * author : junzhang
 * date   : 2019-07-04 16:22
 * desc   :
 */
object ArticlesRemoteDataSource : ArticlesDataSource, FavoritesRemoteDataSource {
    private const val USER_NAME = "name"
    private const val USER_AVATAR = "avatar"
    private const val USER_PROFILE_URL = "profile_url"
    private const val USER_EXTRA_INFO = "extra_info"
    private const val FOLLOW_URL = "follow_url"
    private const val FOLLOW_TITLE = "follow_title"

    private val gson: Gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()

    @WorkerThread
    @Throws(LoginRequiredException::class, SocketTimeoutException::class)
    override fun getForumArticles(query: String, processThreadList: Boolean): ForumPage? {
        return processForumArticlesDocument(
            JsoupClient.sendGetRequestWithQuery(query),
            processThreadList
        )
    }

    @WorkerThread
    override fun getHomePageArticles(param: String, pageNum: Int): List<Article> {
        return try {
            getArticles("$FORUM_BASE_QUERY$param&page=$pageNum")
        } catch (e: Exception) {
            emptyList()
        }
    }

    @Throws(BlockException::class, HttpStatusException::class)
    @WorkerThread
    override fun getPostPreview(query: String): PreviewPost? {
        return try {
            getFirstPost(JsoupClient.sendGetRequestWithQuery(query))
        } catch (e: Exception) {
            when (e) {
                is BlockException,
                is HttpStatusException -> throw e
            }
            Timber.w(e)
            null
        }
    }

    @Throws(BlockException::class, NetworkException::class)
    @WorkerThread
    override fun getArticleDetail(query: String, isFirstFetch: Boolean): ArticleDetail? {
        try {
            val doc = JsoupClient.sendGetRequestWithQuery(query)
            val articleAbstract: ArticleAbstractResponse? =
                doc.selectFirst("script")?.let {
                    try {
                        val json = it.html().trim().replace("\u00a0".toRegex(), "")
                        return@let gson.fromJson(json, ArticleAbstractResponse::class.java)
                    } catch (e: Exception) {
                        // no need to handle
                    }
                    null
                }
            val title = doc.selectFirst("span#thread_subject")?.text().orEmpty()
            if (title.isEmpty()) {
                val message = doc
                    .getElementById("messagetext")
                    ?.nextElementSibling()
                    ?.text()
                    .orEmpty()
                throw BlockException(message)
            }
            val nextPageUrl = doc.selectFirst("a.nxt")?.attr("href").orEmpty()
            val replyAddUrls = getReplyAddUrl(doc)
            val userInfos = getUserInfo(doc)
            val contents = getContents(doc)
            // save current article to history
            if (isFirstFetch) {
                GlobalScope.launch(BackGroundPool) {
                    addToHistory(
                        url = query,
                        title = title,
                        author = userInfos
                            .getOrNull(0)
                            ?.getOrElse(USER_NAME) {
                                ""
                            } ?: "",
//                    content = doc.html() // FIXME use doc.html() as JsoupClient return Document not String
                    )
                }
            }
            val dateTimes = getDateTime(doc)
            val replyUrls = getReplyUrls(doc)
            val postList: MutableList<Post> = ArrayList(userInfos.size)
            for (i in userInfos.indices) {
                try {
                    val replyAddUrl: String? = if (i >= replyAddUrls.size) {
                        null
                    } else {
                        replyAddUrls[i]
                    }
                    val replyUrl: String? = if (i >= replyUrls.size) {
                        null
                    } else {
                        replyUrls[i]
                    }
                    val post = Post(
                        author = userInfos[i][USER_NAME].toString(),
                        avatar = userInfos[i][USER_AVATAR].toString(),
                        date = dateTimes[i],
                        content = contents[i],
                        replyUrl = replyUrl,
                        replyAddUrl = replyAddUrl,
                        profileUrl = userInfos[i][USER_PROFILE_URL].toString(),
                        extraInfo = userInfos[i][USER_EXTRA_INFO],
                        followInfo = userInfos[i][FOLLOW_TITLE]?.to(userInfos[i][FOLLOW_URL] ?: "")
                    )
                    postList.add(post)
                } catch (npe: NullPointerException) {
                    // will skip a loop if there's any npe occurs
                    Timber.v(npe)
                }
            }
            val fromHash = doc.selectFirst("input[name=formhash]")?.attr("value")
            return ArticleDetail(title, postList, nextPageUrl, fromHash, articleAbstract)
        } catch (e: Exception) {
            when (e) {
                // map to NetWorkException
                is SocketTimeoutException -> throw NetworkException()
                is BlockException -> throw e
                else -> throw e
            }
        }
    }

    private suspend fun addToHistory(
        url: String,
        title: String,
        author: String,
        content: String? = null
    ) {
        ArticlesDatabase
            .getInstance()
            .articlesDao()
            .insertHistory(
                HistoryEntity(
                    url = url,
                    title = title,
                    author = author,
                    content = content.orEmpty(),
                    timestamp = System.currentTimeMillis()
                )
            )
    }

    private const val HOT_PATTERN = "热度"
    private const val RECOMMENDED_PATTERN = "评价指数"
    private const val HOT_LIMIT = 100
    private const val RECOMMENDED_LIMIT = 10

    private fun getArticles(query: String): List<Article> {
        var list: List<Article> = emptyList()
        try {
            list = JsoupClient
                .sendGetRequestWithQuery(query)
                .select("tbody[id^=normal]")
                .map { e ->
                    try {
                        val reply = extractFrom(e, "td.num", "a.xi2")
                            .ifBlank { return@map null }
                            .toInt()
                        val view = extractFrom(e, "td.num", "em")
                            .ifBlank { return@map null }
                            .toInt()
                        val title = e.selectFirst("th.common > .xst")?.text().orEmpty()
                        val author = extractFrom(e, "td.by", "a[href*=uid]")
                        val date = extractFrom(e, "td.by", "span")
                        val url = extractAttrFrom(e, "href", "th.common", "a.xst")
                        val origin = e.selectFirst("td.by > a[target]")?.text().orEmpty()
                        val helpInfo = e.select("th.common > span.xi1 > span.xw1")?.text().orEmpty()
                        var helpCoin = 0
                        if (helpInfo.isEmpty()) {
                            if (e.selectFirst("th.common")
                                    ?.text()
                                    ?.contains("- [已解决]") == true
                            ) {
                                helpCoin = -1
                            }
                        } else {
                            helpCoin = try {
                                helpInfo.toInt()
                            } catch (e: Exception) {
                                0
                            }
                        }
                        val isRecommended = if (AppConfig.articleShowRecommendFlag) {
                            e.selectFirst("th.common")
                                ?.getElementsByTag("img")
                                ?.map { it.attr("title") }
                                ?.any { s ->
                                    when {
                                        s.contains(HOT_PATTERN) ->
                                            s.replace(HOT_PATTERN, "")
                                                .trim()
                                                .toIntOrNull()
                                                ?.let { it >= HOT_LIMIT }
                                                ?: false
                                        s.contains(RECOMMENDED_PATTERN) ->
                                            s.replace(RECOMMENDED_PATTERN, "")
                                                .trim()
                                                .toIntOrNull()
                                                ?.let { it >= RECOMMENDED_LIMIT }
                                                ?: false
                                        else -> false
                                    }
                                } ?: false
                        } else {
                            false
                        }
                        if (title.isNotBlank() && author.isNotEmpty()) {
                            return@map Article(
                                title,
                                author,
                                date,
                                url,
                                view,
                                reply,
                                origin,
                                helpCoin,
                                isRecommended
                            )
                        }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                    null
                }
                .filterNotNull()
        } catch (e: Exception) {
            Timber.e(e)
        } finally {
            return list
        }
    }

    @Throws(LoginRequiredException::class)
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
            val articleList: List<Article> = elements.mapNotNull { element ->
                var article: Article? = null
                try {
                    val reply = extractFrom(element, "td.num", "a.xi2").toInt()
                    val view = extractFrom(element, "td.num", "em").toInt()
                    val title = extractFrom(element, "th.new", ".xst").let {
                        if (it.isBlank()) {
                            extractFrom(element, "th.common", ".xst")
                        } else {
                            it
                        }

                    }
                    val author = extractFrom(element, "td.by", "a[href*=uid]")
                    val date = extractFrom(element, "td.by", "span")
                    val url = extractAttrFrom(element, "href", "th.new", "a.xst").let {
                        if (it.isBlank()) {
                            extractAttrFrom(element, "href", "th.common", "a.xst")
                        } else {
                            it
                        }
                    }
                    val origin = extractFrom(element, "td.by", "a[target]")
                    if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(author)) {
                        article = Article(title, author, date, url, view, reply, origin)
                    }
                } catch (nbe: NumberFormatException) {
                    Timber.v(nbe)
                } catch (e: Exception) {
                    Timber.e(e)
                }
                article
            }

            // for thread part
            var threadList: List<ForumThread>? = null
            if (processThreadList) {
                doc.getElementById("thread_types")?.let { threadTypes ->
                    threadList = threadTypes
                        .getElementsByTag("li")
                        .mapNotNull { elementByTag ->
                            try {
                                val element = elementByTag.getElementsByTag("a").first()
                                elements = element.getElementsByTag("span")
                                if (elements.size > 0) {
                                    elements.remove()
                                }
                                val threadUrl = element.attr("href")
                                val name = element.text().trim { it <= ' ' }
                                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(threadUrl)) {
                                    return@mapNotNull ForumThread(name, threadUrl)
                                }
                            } catch (e: Exception) { // don't care
                            }
                            null
                        }

                }
            }
            return ForumPage(articleList, threadList ?: emptyList())
        } catch (e: LoginRequiredException) {
            throw e
        } catch (e: Exception) {
            Timber.e(e)
            return null
        }
    }

    private fun extractFrom(element: Element, vararg tags: String): String {
        if (tags.isNullOrEmpty()) {
            return element.text()
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

    private fun extractAttrFrom(element: Element, attr: String, vararg tags: String): String {
        if (tags.isNullOrEmpty()) {
            return element.text()
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
        val list: MutableList<String> = ArrayList(12)
        try {
            val e = document.getElementById("recommend_add")
            list.add(e.attr("href"))
            val elements = document.select("a.replyadd")
            for (element in elements) {
                list.add(element.attr("href"))
            }
        } catch (npe: NullPointerException) {
            // no need to handle
        }
        return list
    }

    /**
     * get uid/avatar/name
     */
    private fun getUserInfo(document: Document): List<Map<String, String>> {
        val list: MutableList<Map<String, String>> = ArrayList(12)
        val elements = document.select("td[rowspan]")
        for (element in elements) {
            val userInfoMap: MutableMap<String, String> = ArrayMap(4)
            element.select("div.avatar").apply {
                userInfoMap[USER_AVATAR] = select("img").attr("src")
                userInfoMap[USER_PROFILE_URL] = select("a").attr("href")
            }
            element.selectFirst("a[id^=follow]")?.let {
                userInfoMap[FOLLOW_URL] = it.attr("href")
                userInfoMap[FOLLOW_TITLE] = it.attr("title")
            }
            userInfoMap[USER_EXTRA_INFO] = element.getElementsByTag("dl").outerHtml()
            userInfoMap[USER_NAME] = element.select("a.xw1").text()
            list.add(userInfoMap)
        }
        return list
    }

    private fun getDateTime(document: Document): List<String> {
        return document.select("div.authi").select("em").map {
            it.text()
        }
    }

    @Throws(BlockException::class)
    private fun getFirstPost(document: Document): PreviewPost {
        val dateTime = document
            .selectFirst("div.authi")
            ?.select("em")
            ?.text()
            ?: run {
                val message = document
                    .getElementById("messagetext")
                    ?.nextElementSibling()
                    ?.text()
                    .orEmpty()
                throw BlockException(message)
            }
        val content = getFirstContent(document)
        var avatar: String?
        var name: String?
        document.selectFirst("td[rowspan]").apply {
            avatar = selectFirst("div.avatar").selectFirst("img").attr("src")
            name = selectFirst("a.xw1").text()
        }
        return PreviewPost(
            avatar = avatar ?: "",
            author = name ?: "Unknown",
            date = dateTime,
            content = content
        )
    }

    private fun getFirstContent(doc: Document): String {
        return doc.selectFirst("div.pcb")
            .let { element ->
                element.selectFirst("td.t_f")?.let { tmp ->
                    processContentElement(tmp).html()
                }
                    ?: element.selectFirst("div.locked").html()
            }
    }

    private fun getContents(doc: Document): List<String> {
        return doc.select("div.pcb")
            .filterNotNull()
            .mapTo(ArrayList<String>(), {
                it.selectFirst("td.t_f")?.let { tmp ->
                    processContentElement(tmp)
                }?.let { res ->
                    it.select("div.savephotop > img")?.forEach { imgElement ->
                        res.appendChild(
                            imgElement.attr("src", imgElement.attr("file"))
                        )
                    }
                    res.html()
                } ?: it.selectFirst("div.locked").html()
            })
    }

    private fun getReplyUrls(doc: Document): List<String> {
        return doc.getElementsByClass("fastre").map {
            it.attr("href")
        }
    }

    private fun processContentElement(element: Element): Element { // remove picture tips
        element.select("div.tip").remove()
        // remove user level info etc
        element.select("script").remove()
        // convert all code
        for (e in element.getElementsByTag("pre")) {
            val s = e.html()
                .replace("\r\n", "<br/>")
                .replace("\r", "<br/>")
                .replace("\n", "<br/>")
                .replace(" ", "&nbsp;")
            e.html(s)
        }
        // move gif from file to src
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
        return element
    }

    @WorkerThread
    override fun addFavorites(threadId: String, formHash: String): Boolean {
        return try {
            JsoupClient.sendGetRequestWithQuery(
                String.format(
                    ADD_TO_FAVORITE_QUERY,
                    threadId,
                    formHash
                )
            )
            true
        } catch (e: Exception) {
            Timber.e(e)
            false
        }
    }

    /**
     * Support user's post
     * @param query post url without base_server_url
     * @return status message
     */
    @WorkerThread
    fun replyAdd(query: String): String {
        return try {
            val doc = JsoupClient.sendGetRequestWithQuery(query)
            val message = doc.getElementsByClass("nfl").first().text()
            message
        } catch (e: Exception) {
            Timber.e(e)
            "Error"
        }
    }
}