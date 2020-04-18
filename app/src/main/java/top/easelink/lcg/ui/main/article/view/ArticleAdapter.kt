package top.easelink.lcg.ui.main.article.view

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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.synthetic.main.item_post_view.view.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import top.easelink.framework.base.BaseViewHolder
import top.easelink.framework.customview.htmltextview.DrawTableLinkSpan
import top.easelink.framework.customview.htmltextview.HtmlGlideImageGetter
import top.easelink.framework.utils.convertViewToBitmap
import top.easelink.framework.utils.dp2px
import top.easelink.lcg.R
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
import top.easelink.lcg.utils.copyContent
import top.easelink.lcg.utils.saveImageToGallery
import top.easelink.lcg.utils.showMessage
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
        position == mPostList.size -> VIEW_TYPE_LOAD_MORE
        else -> VIEW_TYPE_NORMAL
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> PostViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_post_view,
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
                        R.layout.layout_skelton_article
                        , parent, false
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

    inner class PostViewHolder internal constructor(view: View): BaseViewHolder(view), View.OnClickListener {

        private var post: Post? = null
        private val htmlHttpImageGetter: Html.ImageGetter by lazy {
            HtmlGlideImageGetter(view.context, view.content_text_view)
        }
        override fun onBind(position: Int) {
            post = mPostList[position]
            val postCard = itemView.post_card
            post?.let { p ->
                with(itemView){
                    try {
                        if (p.author == username) {
                            postCard.strokeColor = ContextCompat.getColor(context, R.color.orange)
                            postCard.strokeWidth = dp2px(context, 1f).toInt()
                        } else {
                            postCard.strokeWidth = 0
                        }
                        author_text_view.text = p.author
                        date_text_view.text = p.date
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
                                PopUpProfileDialog(popUpInfo).show(it, PopUpProfileDialog::class.java.simpleName)
                            }?: context.startActivity(
                                Intent(context, ProfileActivity::class.java).also {
                                    it.putExtra(KEY_PROFILE_URL, p.profileUrl)
                                })
                        }
                        Glide.with(context)
                            .load(p.avatar)
                            .transform(RoundedCorners(dp2px(context, 4f).toInt()))
                            .placeholder(R.drawable.ic_noavatar_middle)
                            .error(R.drawable.ic_noavatar_middle_gray)
                            .into(post_avatar)
                        content_text_view.run {
                            setClickableSpecialSpan(ClickableSpecialSpanImpl())
                            val drawTableLinkSpan = DrawTableLinkSpan().also {
                                it.tableLinkText = context.getString(R.string.tap_for_code)
                            }
                            setDrawTableLinkSpan(drawTableLinkSpan)
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
                        if (position == 0) {
                            btn_capture.visibility = View.VISIBLE
                            btn_capture.setOnClickListener(this@PostViewHolder)
                        } else {
                            btn_capture.visibility = View.GONE
                        }
                        if (isLoggedIn) {
                            btn_group.visibility = View.VISIBLE
                            if (TextUtils.isEmpty(p.replyUrl)) {
                                btn_reply.visibility = View.GONE
                            } else {
                                btn_reply.visibility = View.VISIBLE
                                btn_reply.setOnClickListener(this@PostViewHolder)
                            }
                            if (TextUtils.isEmpty(p.replyAddUrl)) {
                                btn_thumb_up.visibility = View.GONE
                            } else {
                                btn_thumb_up.visibility = View.VISIBLE
                                btn_thumb_up.setOnClickListener(this@PostViewHolder)
                            }
                            btn_copy.setOnClickListener(this@PostViewHolder)
                        } else {
                            btn_group.visibility = View.GONE
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
                    R.id.btn_reply -> p.replyUrl?.let {
                        EventBus.getDefault().post(ReplyPostEvent(it, p.author))
                    }
                    R.id.btn_copy -> if (copyContent(p.content, p.author)) {
                        showMessage(R.string.copy_succeed)
                    } else {
                        showMessage(R.string.copy_failed)
                    }
                    R.id.btn_thumb_up -> p.replyAddUrl?.let {
                        mListener.replyAdd(it)
                    }
                    R.id.btn_capture -> {
                        sendEvent(EVENT_CAPTURE_ARTICLE)
                        convertViewToBitmap(itemView, Bitmap.Config.ARGB_8888)?.let {
                            val path = saveImageToGallery(it, System.currentTimeMillis().toString())
                            EventBus.getDefault().post(ScreenCaptureEvent(path))
                        }?: showMessage(R.string.general_error)
                    }
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    class PostEmptyViewHolder internal constructor(view: View?) :
        BaseViewHolder(view) {
        override fun onBind(position: Int) {}
    }

    inner class LoadMoreViewHolder internal constructor(private val mView: View) :
        BaseViewHolder(mView) {
        override fun onBind(position: Int) {
            mListener.fetchArticlePost(ArticleAdapterListener.FETCH_POST_MORE) { res ->
                mView.visibility = if (res) View.GONE else View.VISIBLE
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_EMPTY = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_LOAD_MORE = 2
    }

}