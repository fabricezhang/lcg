package top.easelink.lcg.ui.main.article.view

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.load
import kotlinx.android.synthetic.main.dialog_post_preview.*
import timber.log.Timber
import top.easelink.framework.customview.htmltextview.HtmlCoilImageGetter
import top.easelink.framework.topbase.TopDialog
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.R
import top.easelink.lcg.appinit.LCGApp
import top.easelink.lcg.ui.main.article.viewmodel.PostPreviewViewModel
import top.easelink.lcg.utils.getScreenWidthDp

class PostPreviewDialog : TopDialog() {

    private lateinit var mViewModel: PostPreviewViewModel

    companion object {
        val TAG: String = PostPreviewDialog::class.java.simpleName
        private const val ARTICLE_QUERY = "article_query"

        @JvmStatic
        fun newInstance(url: String): PostPreviewDialog {
            return PostPreviewDialog().apply {
                arguments = Bundle().apply {
                    putString(ARTICLE_QUERY, url)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel = ViewModelProvider(this)[PostPreviewViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_post_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exit.setOnClickListener {
            dismissDialog()
        }
        dialog?.setCanceledOnTouchOutside(true)
        initView()
    }

    private fun initView() {
        try {
            arguments
                ?.getString(ARTICLE_QUERY)
                ?.takeIf { it.isNotBlank() }
                ?.let { query ->
                    mViewModel.content.observe(viewLifecycleOwner, Observer {
                        content_text_view.setHtml(
                            it, HtmlCoilImageGetter(
                                content_text_view.context,
                                content_text_view,
                                this
                            )
                        )
                    })
                    mViewModel.author.observe(viewLifecycleOwner, Observer {
                        author_text_view.text = it
                    })
                    mViewModel.avatar.observe(viewLifecycleOwner, Observer {
                        post_avatar.load(it)
                    })
                    mViewModel.date.observe(viewLifecycleOwner, Observer {
                        date_text_view.text = it
                    })
                    mViewModel.loadingResult.observe(viewLifecycleOwner, Observer {
                        if (it == -1) {
                            loading_status.visibility = View.GONE
                        } else {
                            loading_status.visibility = View.VISIBLE
                            loading_info.setText(it)
                        }
                    })
                    mViewModel.initUrl(query)
                }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = (getScreenWidthDp(this@PostPreviewDialog.mContext)
                .dpToPx(this@PostPreviewDialog.mContext) * 0.95)
                .toInt()
            height = 400.dpToPx(LCGApp.context).toInt()
            gravity = Gravity.CENTER
        }
    }

}
