package top.easelink.lcg.ui.main.article.view

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import top.easelink.framework.base.SafeShowDialogFragment
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.article.viewmodel.ReplyPostViewModel

class CommentArticleDialog : SafeShowDialogFragment() {

    private lateinit var replyPostViewModel: ReplyPostViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setWindowAnimations(R.style.BottomInOutAnim)
        return inflater.inflate(R.layout.dialog_comment_article, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        replyPostViewModel = ViewModelProviders.of(this).get(ReplyPostViewModel::class.java)
        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dismissDialog()
        }
        val button = view.findViewById<Button>(R.id.btn_confirm)
        button.setOnClickListener {
            val content = view.findViewById<EditText>(R.id.reply_content).text.trimEnd()
            replyPostViewModel.sending.observe(this, object : Observer<Boolean> {
                var lastState: Boolean = false
                override fun onChanged(newState: Boolean) {
                    if (lastState != newState) {
                        lastState = newState
                        if (newState) {
                            button.setText(R.string.reply_post_btn_sending)
                        } else {
                            button.setText(R.string.reply_post_btn_sent)
                        }
                    }
                }
            })
            replyPostViewModel.sendReply(
                arguments?.getString(REPLY_POST_URL),
                content.toString()
            ) {view.postDelayed({
                dismissDialog()
            }, 1000L)}

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.BOTTOM
        }
    }

    fun show(fragmentManager: FragmentManager?) {
        super.show(fragmentManager!!, TAG)
    }

    companion object {
        private val TAG = CommentArticleDialog::class.java.simpleName
        private const val REPLY_POST_URL = "reply_article_url"
        @JvmStatic
        fun newInstance(replyPostUrl: String): CommentArticleDialog {
            return CommentArticleDialog().apply {
                arguments = Bundle().apply {
                    putString(REPLY_POST_URL, replyPostUrl)
                }
            }
        }
    }
}