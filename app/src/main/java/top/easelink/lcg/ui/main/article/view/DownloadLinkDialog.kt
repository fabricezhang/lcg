package top.easelink.lcg.ui.main.article.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import kotlinx.android.synthetic.main.dialog_download_link.*
import top.easelink.framework.base.SafeShowDialogFragment
import top.easelink.lcg.R
import top.easelink.lcg.utils.showMessage
import java.util.*

class DownloadLinkDialog : SafeShowDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.dialog_download_link, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.apply {
            val linkList = getStringArrayList(KEY_LINK_LIST)
            if (linkList != null && !linkList.isEmpty()) {
                download_link_list.run {
                    adapter = ArrayAdapter(
                        mContext,
                        R.layout.download_link_item_view, linkList
                    )
                    onItemClickListener =
                        AdapterView.OnItemClickListener { _: AdapterView<*>?, itemView: View, position: Int, _: Long ->
                            copy(linkList[position])
                            itemView.setBackgroundColor(mContext.resources.getColor(R.color.colorAccent))
                        }
                }
            }
        }
        view.findViewById<View>(R.id.exit).setOnClickListener { dismissDialog() }
    }

    fun show(fragmentManager: FragmentManager?) {
        if (fragmentManager != null) {
            super.show(fragmentManager, TAG)
        }
    }

    private fun copy(clipString: String) {
        val cm = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText("DownloadLink", clipString)
        cm.primaryClip = mClipData
        showMessage(R.string.copy_succeed)
    }

    companion object {
        private val TAG: String = DownloadLinkDialog::class.java.simpleName
        private const val KEY_LINK_LIST = "KEY_LINK_LIST"
        @JvmStatic
        fun newInstance(downloadLinkList: ArrayList<String>): DownloadLinkDialog {
            val fragment = DownloadLinkDialog()
            val bundle = Bundle()
            bundle.putStringArrayList(KEY_LINK_LIST, downloadLinkList)
            fragment.arguments = bundle
            return fragment
        }
    }
}