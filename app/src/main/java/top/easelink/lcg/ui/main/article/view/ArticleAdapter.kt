package top.easelink.lcg.ui.main.article.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_post_view.view.*
import kotlinx.android.synthetic.main.item_reply_view.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.customview.htmltextview.DrawPreCodeSpan
import top.easelink.framework.customview.htmltextview.HtmlCoilImageGetter
import top.easelink.framework.threadpool.Main
import top.easelink.framework.utils.convertViewToBitmap
import top.easelink.framework.utils.dp2px
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.config.AppConfig
import top.easelink.lcg.mta.EVENT_CAPTURE_ARTICLE
import top.easelink.lcg.mta.sendEvent
import top.easelink.lcg.spipedata.UserData.isLoggedIn
import top.easelink.lcg.spipedata.UserData.username
import top.easelink.lcg.ui.main.article.viewmodel.ArticleAdapterListener
import top.easelink.lcg.ui.main.model.OpenArticleEvent
import top.easelink.lcg.ui.main.model.OpenLargeImageViewEvent
import top.easelink.lcg.ui.main.model.ReplyPostEvent
import top.easelink.lcg.ui.main.model.ScreenCaptureEvent
import top.easelink.lcg.ui.main.source.model.Post
import top.easelink.lcg.ui.profile.model.PopUpProfileInfo
import top.easelink.lcg.ui.profile.view.KEY_PROFILE_URL
import top.easelink.lcg.ui.profile.view.PopUpProfileDialog
import top.easelink.lcg.ui.profile.view.ProfileActivity
import top.easelink.lcg.ui.webview.view.WebViewActivity
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.avatar.getAvatar
import top.easelink.lcg.utils.copyContent
import top.easelink.lcg.utils.saveImageToGallery
import top.easelink.lcg.utils.showMessage
import top.easelink.lcg.utils.toTimeStamp
import java.lang.ref.WeakReference
import java.util.*

class ArticleAdapter(
    private val mListener: ArticleAdapterListener
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val mPostList: MutableList<Post> = ArrayList()
    private var fragmentManager: WeakReference<FragmentManager>? = null

    override fun getItemCount() = when {
        mPostList.isEmpty() -> 1 // show empty view
        mPostList.size > 10 -> mPostList.size + 1 // for post more than 10 add a load more item
        else -> mPostList.size
    }

    override fun getItemViewType(position: Int) = when {
        mPostList.isEmpty() -> VIEW_TYPE_EMPTY
        position == 0 -> VIEW_TYPE_POST
        position == mPostList.size -> VIEW_TYPE_LOAD_MORE
        else -> VIEW_TYPE_REPLY
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_POST -> PostViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_post_view,
                        parent, false
                    )
            )
            VIEW_TYPE_REPLY -> ReplyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_reply_view,
                        parent, false
                    )
            )
            VIEW_TYPE_LOAD_MORE -> LoadMoreViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.item_load_more_view,
                        parent, false
                    )
            )
            else -> PostEmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.layout_skelton_article, parent, false
                    )
            )
        }
    }

    fun addItems(postList: List<Post>) {
        mPostList.addAll(postList)
        notifyDataSetChanged()
    }

    fun clearItems() {
        mPostList.clear()
        notifyDataSetChanged()
    }

    fun setFragmentManager(fragmentManager: FragmentManager) {
        this.fragmentManager = WeakReference(fragmentManager)
    }

    inner class PostViewHolder internal constructor(
        view: View
    ) : BaseViewHolder(view), View.OnClickListener {

        private var post: Post? = null
        private val htmlHttpImageGetter: Html.ImageGetter by lazy {
            HtmlCoilImageGetter(view.context, view.content_text_view)
        }

        override fun onBind(position: Int) {
            post = mPostList[position]
            post?.let { p ->
                with(itemView) {
                    try {
                        author_text_view.text = p.author
                        date_text_view.text = getDateDiff(p.date)
                        post_avatar.setOnClickListener { _ ->
                            fragmentManager?.get()?.let {
                                val location = IntArray(2)
                                post_avatar.getLocationInWindow(location)
                                val popUpInfo = PopUpProfileInfo(
                                    location[0],
                                    location[1],
                                    p.avatar,
                                    p.author,
                                    p.extraInfo,
                                    p.followInfo,
                                    p.profileUrl
                                )
                                PopUpProfileDialog(popUpInfo).show(
                                    it,
                                    PopUpProfileDialog::class.java.simpleName
                                )
                            } ?: context.startActivity(
                                Intent(context, ProfileActivity::class.java).also {
                                    it.putExtra(KEY_PROFILE_URL, p.profileUrl)
                                })
                        }
                        post_avatar.load(p.avatar) {
                            transformations(RoundedCornersTransformation(4.dpToPx(context)))
                            error(R.drawable.ic_noavatar_middle_gray)
                        }
                        content_text_view.run {
                            if (AppConfig.articleHandlePreTag) {
                                setClickableSpecialSpan(ClickableSpecialSpanImpl())
                                val drawTableLinkSpan = DrawPreCodeSpan()
                                    .also {
                                        it.tableLinkText = context.getString(R.string.tap_for_code)
                                    }
                                setDrawPreCodeSpan(drawTableLinkSpan)
                            } else {
                                setClickableSpecialSpan(null)
                                setDrawPreCodeSpan(null)
                            }
                            setImageTagClickListener { _: Context, imageUrl: String, _: Int ->
                                EventBus.getDefault().post(OpenLargeImageViewEvent(imageUrl))
                            }
                            setOnLinkTagClickListener { c: Context?, url: String ->
                                if (url.startsWith(SERVER_BASE_URL + "thread")) {
                                    EventBus.getDefault()
                                        .post(OpenArticleEvent(url.substring(SERVER_BASE_URL.length)))
                                } else {
                                    WebViewActivity.startWebViewWith(url, c)
                                }
                            }
                            setHtml(p.content, htmlHttpImageGetter)
                        }
                        post_btn_capture.visibility = View.VISIBLE
                        post_btn_capture.setOnClickListener(this@PostViewHolder)
                        if (isLoggedIn) {
                            post_btn_group.visibility = View.VISIBLE
                            if (TextUtils.isEmpty(p.replyUrl)) {
                                post_btn_reply.visibility = View.GONE
                            } else {
                                post_btn_reply.visibility = View.VISIBLE
                                post_btn_reply.setOnClickListener(this@PostViewHolder)
                            }
                            if (TextUtils.isEmpty(p.replyAddUrl)) {
                                post_btn_thumb_up.visibility = View.GONE
                            } else {
                                post_btn_thumb_up.visibility = View.VISIBLE
                                post_btn_thumb_up.setOnClickListener(this@PostViewHolder)
                            }
                            post_btn_copy.setOnClickListener(this@PostViewHolder)
                        } else {
                            post_btn_group.visibility = View.GONE
                        }
                        // fix issue occurs on some manufactures os, like MIUI
                        content_text_view.setOnLongClickListener { true }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }

        override fun onClick(v: View) {
            post?.let { p ->
                when (v.id) {
                    R.id.post_btn_reply -> p.replyUrl?.let {
                        EventBus.getDefault().post(ReplyPostEvent(it, p.author))
                    }
                    R.id.post_btn_copy -> if (copyContent(p.content, p.author)) {
                        showMessage(R.string.copy_succeed)
                    } else {
                        showMessage(R.string.copy_failed)
                    }
                    R.id.post_btn_thumb_up -> p.replyAddUrl?.let {
                        mListener.replyAdd(it)
                    }
                    R.id.post_btn_capture -> {
                        sendEvent(EVENT_CAPTURE_ARTICLE)
                        convertViewToBitmap(itemView, Bitmap.Config.ARGB_8888)?.let {
                            val path = saveImageToGallery(it, System.currentTimeMillis().toString())
                            EventBus.getDefault().post(ScreenCaptureEvent(path))
                        } ?: showMessage(R.string.general_error)
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    inner class ReplyViewHolder internal constructor(view: View) : BaseViewHolder(view),
        View.OnClickListener {

        private var post: Post? = null
        private val htmlHttpImageGetter: Html.ImageGetter by lazy {
            HtmlCoilImageGetter(view.context, view.reply_content_text_view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBind(position: Int) {
            post = mPostList[position]
            post?.let { p ->
                with(itemView) {
                    try {
                        if (p.author == username) {
                            reply_card.strokeColor = ContextCompat.getColor(context, R.color.orange)
                            reply_card.strokeWidth = dp2px(context, 1f).toInt()
                        } else {
                            reply_card.strokeWidth = 0
                        }
                        reply_position.text = "#$position"
                        reply_author_text_view.text = p.author
                        reply_date_text_view.text = getDateDiff(p.date)
                        reply_avatar.setOnClickListener { _ ->
                            fragmentManager?.get()?.let {
                                val location = IntArray(2)
                                reply_avatar.getLocationInWindow(location)
                                val popUpInfo = PopUpProfileInfo(
                                    location[0],
                                    location[1],
                                    p.avatar,
                                    p.author,
                                    p.extraInfo,
                                    p.followInfo,
                                    p.profileUrl
                                )
                                PopUpProfileDialog(popUpInfo).show(
                                    it,
                                    PopUpProfileDialog::class.java.simpleName
                                )
                            } ?: context.startActivity(
                                Intent(context, ProfileActivity::class.java).also {
                                    it.putExtra(KEY_PROFILE_URL, p.profileUrl)
                                })
                        }
                        reply_avatar.load(p.avatar) {
                            crossfade(true)
                            transformations(RoundedCornersTransformation(6.dpToPx(context)))
                            placeholder(R.drawable.ic_avatar_placeholder)
                            error(getAvatar())
                        }
                        reply_content_text_view.run {
                            if (AppConfig.articleHandlePreTag) {
                                setClickableSpecialSpan(ClickableSpecialSpanImpl())
                                val drawTableLinkSpan = DrawPreCodeSpan()
                                    .also {
                                        it.tableLinkText = context.getString(R.string.tap_for_code)
                                    }
                                setDrawPreCodeSpan(drawTableLinkSpan)
                            } else {
                                setClickableSpecialSpan(null)
                                setDrawPreCodeSpan(null)
                            }
                            setImageTagClickListener { _: Context, imageUrl: String, _: Int ->
                                EventBus.getDefault().post(OpenLargeImageViewEvent(imageUrl))
                            }
                            setOnLinkTagClickListener { c: Context?, url: String ->
                                if (url.startsWith(SERVER_BASE_URL + "thread")) {
                                    EventBus.getDefault()
                                        .post(OpenArticleEvent(url.substring(SERVER_BASE_URL.length)))
                                } else {
                                    WebViewActivity.startWebViewWith(url, c)
                                }
                            }
                            setHtml(p.content, htmlHttpImageGetter)
                        }
                        if (isLoggedIn) {
                            reply_btn_group.visibility = View.VISIBLE
                            if (TextUtils.isEmpty(p.replyUrl)) {
                                reply_btn_reply.visibility = View.GONE
                            } else {
                                reply_btn_reply.visibility = View.VISIBLE
                                reply_btn_reply.setOnClickListener(this@ReplyViewHolder)
                            }
                            if (TextUtils.isEmpty(p.replyAddUrl)) {
                                reply_btn_thumb_up.visibility = View.GONE
                            } else {
                                reply_btn_thumb_up.visibility = View.VISIBLE
                                reply_btn_thumb_up.setOnClickListener(this@ReplyViewHolder)
                            }
                            reply_btn_copy.setOnClickListener(this@ReplyViewHolder)
                        } else {
                            reply_btn_group.visibility = View.GONE
                        }
                        // fix issue occurs on some manufactures os, like MIUI
                        reply_content_text_view.setOnLongClickListener { true }
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }

        override fun onClick(v: View) {
            post?.let { p ->
                when (v.id) {
                    R.id.reply_btn_reply -> p.replyUrl?.let {
                        EventBus.getDefault().post(ReplyPostEvent(it, p.author))
                    }
                    R.id.reply_btn_copy -> if (copyContent(p.content, p.author)) {
                        showMessage(R.string.copy_succeed)
                    } else {
                        showMessage(R.string.copy_failed)
                    }
                    R.id.reply_btn_thumb_up -> p.replyAddUrl?.let {
                        mListener.replyAdd(it)
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    fun getDateDiff(date: String): String {
        val tips = "发表于 "
        return date.replace(tips, "").toTimeStamp()?.let {
            val diff = System.currentTimeMillis() - it
            when (val minutesDiff = diff / 1000 / 60) {
                in 0..1 -> return "${tips}1 分钟前"
                in 1..60 -> return "${tips}${minutesDiff} 分钟前"
                in 60..24 * 60 -> {
                    val hoursDiff = diff / 1000 / 60 / 60
                    return "${tips}${hoursDiff} 小时前"
                }
                in 24 * 60..7 * 24 * 60 -> {
                    val dayDiff = diff / 1000 / 60 / 60 / 24
                    return "${tips}${dayDiff} 天前"
                }
                else -> {
                    date
                }
            }
        } ?: date
    }

    class PostEmptyViewHolder internal constructor(view: View?) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {}
    }

    inner class LoadMoreViewHolder internal constructor(private val mView: View) :
        BaseViewHolder(mView) {
        override fun onBind(position: Int) {
            mListener.fetchArticlePost(ArticleAdapterListener.FETCH_POST_MORE) { res ->
                GlobalScope.launch(Main) {
                    mView.visibility = if (res) View.GONE else View.VISIBLE
                }
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_REPLY = 1
        private const val VIEW_TYPE_POST = 2
        private const val VIEW_TYPE_LOAD_MORE = 3
    }

}