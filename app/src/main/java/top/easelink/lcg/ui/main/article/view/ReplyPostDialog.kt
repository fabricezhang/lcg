package top.easelink.lcg.ui.main.article.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import top.easelink.framework.base.BaseDialog
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.article.viewmodel.ReplyPostViewModel
import top.easelink.lcg.utils.ActivityUtils

class ReplyPostDialog : BaseDialog() {

    private lateinit var replyPostViewModel: ReplyPostViewModel

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
        val bundle = arguments
        view.findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            dismissDialog()
        }
        view.findViewById<Button>(R.id.btn_confirm).setOnClickListener {
            val content = view.findViewById<EditText>(R.id.reply_content).text.trimEnd()
            replyPostViewModel.sendReply(bundle?.getString(REPLY_POST_URL), content.toString())
        }
    }

    fun show(fragmentManager: FragmentManager?) {
        super.show(fragmentManager!!, TAG)
    }

    companion object {
        private val TAG = ActivityUtils.TAG_PREFIX + ReplyPostDialog::class.java.simpleName
        private const val REPLY_POST_URL = "REPLY_POST_URL"
        @JvmStatic
        fun newInstance(replyPostUrl: String?): ReplyPostDialog {
            return ReplyPostDialog().apply {
                arguments = Bundle().apply {
                    putString(REPLY_POST_URL, replyPostUrl)
                }
            }
        }
    }
}