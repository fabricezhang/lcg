package top.easelink.lcg.ui.main.article.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_article.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import top.easelink.framework.topbase.ControllableFragment
import top.easelink.framework.topbase.TopFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.article.view.DownloadLinkDialog.Companion.newInstance
import top.easelink.lcg.ui.main.article.view.ReplyPostDialog.Companion.newInstance
import top.easelink.lcg.ui.main.article.view.ScreenCaptureDialog.Companion.TAG
import top.easelink.lcg.ui.main.article.viewmodel.ArticleAdapterListener.Companion.FETCH_POST_INIT
import top.easelink.lcg.ui.main.article.viewmodel.ArticleViewModel
import top.easelink.lcg.ui.main.model.ReplyPostEvent
import top.easelink.lcg.ui.main.model.ScreenCaptureEvent
import top.easelink.lcg.ui.main.source.model.Post
import top.easelink.lcg.ui.webview.view.WebViewActivity
import top.easelink.lcg.utils.WebsiteConstant
import top.easelink.lcg.utils.showMessage

class ArticleFragment: TopFragment(), ControllableFragment {

    companion object {
        fun newInstance(url: String): ArticleFragment {
            val args = Bundle().apply {
                putString(ARTICLE_URL, url)
            }
            val fragment = ArticleFragment()
            fragment.arguments = args
            return fragment
        }

        private const val ARTICLE_URL = "article_url"
        const val REPLY_POST_RESULT = 1000
    }

    private lateinit var viewModel: ArticleViewModel
    private var articleUrl: String = ""

    override fun isControllable(): Boolean {
        return true
    }

    override fun getBackStackTag(): String {
        return articleUrl
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articleUrl = arguments?.getString(ARTICLE_URL).orEmpty()
        EventBus.getDefault().register(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ArticleViewModel::class.java]
        initObserver()
        setUp()
        setupToolBar()
        viewModel.setUrl(articleUrl)
        viewModel.fetchArticlePost(FETCH_POST_INIT)
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    private fun initObserver() {
        viewModel.articleTitle.observe(viewLifecycleOwner) {
            article_toolbar.title = it
        }
        viewModel.shouldDisplayPosts.observe(viewLifecycleOwner) {
            post_recycler_view.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.blockMessage.observe(viewLifecycleOwner) {
            block_text.text = it
            block_container.visibility = if (it.isNotEmpty()) View.VISIBLE else View.GONE
        }
        viewModel.isNotFound.observe(viewLifecycleOwner) {
            not_found_container.visibility = if (it) View.VISIBLE else View.GONE
        }
        viewModel.isLoading.observe(viewLifecycleOwner) {
            fetching_progress_bar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun setUp() {
        post_recycler_view.apply {
            val mLayoutManager = LinearLayoutManager(context).also {
                it.orientation = RecyclerView.VERTICAL
            }
            layoutManager = mLayoutManager
            itemAnimator = DefaultItemAnimator()
            adapter = ArticleAdapter(
                viewModel, this@ArticleFragment
            )

            viewModel.posts.observe(viewLifecycleOwner) {
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
            }
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
                        viewModel.extractDownloadUrl()
                            ?.takeIf { list -> list.isNotEmpty() }
                            ?.run {
                                newInstance(this).show(if (isAdded) parentFragmentManager else childFragmentManager)
                            } ?: showMessage(R.string.download_link_not_found)
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
        when (requestCode) {
            REPLY_POST_RESULT -> {
                if (resultCode == 1) {
                    data?.getBundleExtra("post")
                        ?.getParcelable<Post>("post")
                        ?.let {
                            viewModel.addPostToTop(it)
                            post_recycler_view.scrollToPosition(1)
                        }
                    showMessage(R.string.reply_post_succeed)
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
        ScreenCaptureDialog
            .newInstance(event.imagePath)
            .show(
                fragmentManager = if (isAdded) parentFragmentManager else childFragmentManager,
                tag = TAG
            )
    }
}