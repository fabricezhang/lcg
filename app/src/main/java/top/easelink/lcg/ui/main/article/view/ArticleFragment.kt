package top.easelink.lcg.ui.main.article.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_article.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.easelink.framework.base.BaseFragment
import top.easelink.lcg.BR
import top.easelink.lcg.R
import top.easelink.lcg.config.AppConfig
import top.easelink.lcg.databinding.FragmentArticleBinding
import top.easelink.lcg.ui.main.article.view.DownloadLinkDialog.Companion.newInstance
import top.easelink.lcg.ui.main.article.view.ReplyPostDialog.Companion.newInstance
import top.easelink.lcg.ui.main.article.view.ScreenCaptureDialog.Companion.TAG
import top.easelink.lcg.ui.main.article.viewmodel.ArticleAdapter
import top.easelink.lcg.ui.main.article.viewmodel.ArticleAdapterListener.Companion.FETCH_POST_INIT
import top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel
import top.easelink.lcg.ui.main.model.ReplyPostEvent
import top.easelink.lcg.ui.main.model.ScreenCaptureEvent
import top.easelink.lcg.ui.webview.view.WebViewActivity
import top.easelink.lcg.utils.WebsiteConstant
import top.easelink.lcg.utils.showMessage
import java.util.*

class ArticleFragment(private var articleUrl: String) : BaseFragment<FragmentArticleBinding, ArticleViewModel>() {

    // try fix no empty constructor issue
    constructor() : this(AppConfig.getAppReleaseUrl())

    companion object {
        const val REPLY_POST_RESULT = 1000
    }

    override fun isControllable(): Boolean {
        return true
    }

    override fun getBackStackTag(): String {
        return articleUrl
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_article
    }

    override fun getViewModel(): ArticleViewModel {
        return ViewModelProvider(this)[ArticleViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        setupToolBar()
        viewModel.setUrl(articleUrl)
        viewModel.fetchArticlePost(FETCH_POST_INIT){}
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun setUp() {
        viewDataBinding.postRecyclerView.apply {
            val mLayoutManager = LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = ArticleAdapter(viewModel).also {
                it.setFragmentManager(childFragmentManager)
            }

            viewModel.posts.observe(viewLifecycleOwner, androidx.lifecycle.Observer{
                val url = it[0].replyUrl
                if (it.size > 0 && url != null) {
                    comment.apply {
                        visibility = View.VISIBLE
                    }.setOnClickListener {
                        val dialog = CommentArticleDialog.newInstance(url)
                        dialog.setTargetFragment(this@ArticleFragment, REPLY_POST_RESULT)
                        dialog.show(if (isAdded) parentFragmentManager else childFragmentManager)
                    }
                } else {
                    comment.visibility = View.GONE
                }
                (adapter as? ArticleAdapter)?.run {
                    clearItems()
                    addItems(it)
                }
            })
        }
    }

    private fun setupToolBar() {
        article_toolbar.apply {
            inflateMenu(R.menu.article)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_open_in_webview -> WebViewActivity.startWebViewWith(
                        WebsiteConstant.SERVER_BASE_URL + articleUrl,
                        context
                    )
                    R.id.action_extract_urls -> {
                        val linkList: ArrayList<String>? = viewModel.extractDownloadUrl()
                        if (linkList != null && linkList.isNotEmpty()) {
                            newInstance(linkList).show(if (isAdded) parentFragmentManager else childFragmentManager)
                        } else {
                            showMessage(R.string.download_link_not_found)
                        }
                    }
                    R.id.action_add_to_my_favorite -> viewModel.addToFavorite()
                    else -> {
                    }
                }
                return@setOnMenuItemClickListener true
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REPLY_POST_RESULT -> {
                if (resultCode == 1) {
                    data?.getBundleExtra("post")
                        ?.getParcelable<top.easelink.lcg.ui.main.source.model.Post>("post")
                        ?.let {
                            viewModel.addPostToTop(it)
                            viewDataBinding.postRecyclerView.scrollToPosition(1)
                        }
                } else {
                    showMessage(R.string.reply_post_failed)
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ReplyPostEvent) {
        newInstance(event.replyUrl, event.author).show(
            if (isAdded) parentFragmentManager else childFragmentManager
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ScreenCaptureEvent) {
        ScreenCaptureDialog.newInstance(event.imagePath).show(
            if (isAdded) parentFragmentManager else childFragmentManager,
            TAG
        )
    }
}