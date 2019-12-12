package top.easelink.lcg.ui.main.article.view

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import top.easelink.framework.base.BaseDialog
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.article.viewmodel.ReplyPostViewModel
import top.easelink.lcg.utils.TAG_PREFIX

class ReplyPostDialog : BaseDialog() {

    private lateinit var replyPostViewModel: ReplyPostViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_Dialog_FullScreen_BottomInOut)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_reply_post, container, false)
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
        view.findViewById<TextView>(R.id.reply_to).text =
            String.format(
                getString(R.string.reply_post_dialog_title),
                arguments?.getString(REPLY_POST_AUTHOR)
            )
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
        private val TAG = TAG_PREFIX + ReplyPostDialog::class.java.simpleName
        private const val REPLY_POST_URL = "reply_post_url"
        private const val REPLY_POST_AUTHOR = "reply_post_author"
        @JvmStatic
        fun newInstance(replyPostUrl: String, author: String): ReplyPostDialog {
            return ReplyPostDialog().apply {
                arguments = Bundle().apply {
                    putString(REPLY_POST_URL, replyPostUrl)
                    putString(REPLY_POST_AUTHOR, author)
                }
            }
        }
    }
}