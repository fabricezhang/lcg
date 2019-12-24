package top.easelink.lcg.ui.main.article.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_post_preview.*
import timber.log.Timber
import top.easelink.framework.base.SafeShowDialogFragment
import top.easelink.framework.customview.htmltextview.HtmlGlideImageGetter
import top.easelink.framework.utils.dpToPx
import top.easelink.lcg.LCGApp
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.article.viewmodel.PostPreviewViewModel
import top.easelink.lcg.utils.TAG_PREFIX

class PostPreviewDialog : SafeShowDialogFragment() {

    private lateinit var mViewModel: PostPreviewViewModel

    companion object {
        val TAG = TAG_PREFIX + PostPreviewDialog::class.java.simpleName
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
        mViewModel = ViewModelProviders.of(this).get(PostPreviewViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_post_preview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            exit.setOnClickListener {
                dismissDialog()
            }
            val query = arguments?.getString(ARTICLE_QUERY)
            if (query?.isNotBlank() == true) {
                mViewModel.content.observe(this, Observer {
                    content_text_view.setHtml(it, HtmlGlideImageGetter(
                        content_text_view.context,
                        content_text_view
                    ))
                })
                mViewModel.author.observe(this, Observer {
                    author_text_view.text = it
                })
                mViewModel.avatar.observe(this, Observer {
                    Glide.with(this)
                        .load(it)
                        .into(post_avatar)
                })
                mViewModel.date.observe(this, Observer {
                    date_text_view.text = it
                })
                mViewModel.loadingResult.observe(this, Observer {
                    if (it == -1) {
                        loading_status.visibility = View.GONE
                    } else {
                        loading_status.visibility = View.VISIBLE
                        loading_info.setText(it)
                    }
                })
                mViewModel.initUrl(query)
            }
            dialog?.setCanceledOnTouchOutside(true)
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = 400.dpToPx(LCGApp.getContext()).toInt()
            gravity = Gravity.CENTER
        }
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 0.9f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 0.9f),
                ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
            )
            interpolator = AnticipateOvershootInterpolator()
            duration = 800
        }.start()
    }

}
