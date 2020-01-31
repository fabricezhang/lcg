package top.easelink.lcg.ui.main.forumnav.view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kunminx.linkage.adapter.viewholder.LinkagePrimaryViewHolder
import com.kunminx.linkage.contract.ILinkagePrimaryAdapterConfig
import top.easelink.lcg.R

internal class ForumsPrimaryAdapterConfig : ILinkagePrimaryAdapterConfig {
    private lateinit var mContext: Context

    override fun setContext(context: Context) {
        mContext = context
    }

    override fun getLayoutId(): Int {
        return com.kunminx.linkage.R.layout.default_adapter_linkage_primary
    }

    override fun getGroupTitleViewId(): Int {
        return com.kunminx.linkage.R.id.tv_group
    }

    override fun getRootViewId(): Int {
        return com.kunminx.linkage.R.id.layout_group
    }

    override fun onBindViewHolder(
        holder: LinkagePrimaryViewHolder,
        selected: Boolean,
        title: String
    ) {
        val tvTitle = holder.mGroupTitle as TextView
        tvTitle.text = title
        tvTitle.setBackgroundColor(
            mContext.resources.getColor(
                if (selected) R.color.colorPurple else R.color.colorWhite
            )
        )
        tvTitle.setTextColor(
            ContextCompat.getColor(
                mContext,
                if (selected) R.color.colorWhite else R.color.colorGray
            )
        )
    }

    override fun onItemClick(
        holder: LinkagePrimaryViewHolder,
        view: View,
        title: String
    ) {

    }
}