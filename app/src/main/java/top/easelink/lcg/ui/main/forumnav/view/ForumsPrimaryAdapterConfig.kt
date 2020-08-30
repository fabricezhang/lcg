package top.easelink.lcg.ui.main.forumnav.view

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import top.easelink.framework.customview.linkagerv.adapter.viewholder.LinkagePrimaryViewHolder
import top.easelink.framework.customview.linkagerv.contract.ILinkagePrimaryAdapterConfig
import top.easelink.lcg.R

internal class ForumsPrimaryAdapterConfig : ILinkagePrimaryAdapterConfig {
    private lateinit var mContext: Context

    override fun setContext(context: Context) {
        mContext = context
    }

    override fun getLayoutId(): Int {
        return R.layout.item_forum_primary_linear
    }

    override fun getGroupTitleViewId(): Int {
        return R.id.primary_title
    }

    override fun getRootViewId(): Int {
        return R.id.primary_container
    }

    override fun onBindViewHolder(
        holder: LinkagePrimaryViewHolder,
        selected: Boolean,
        title: String
    ) {
        val tvTitle = holder.mGroupTitle as TextView
        tvTitle.text = title
        tvTitle.setBackgroundColor(
            ContextCompat.getColor(
                mContext,
                if (selected) R.color.white else R.color.slight_light_gray
            )
        )
        tvTitle.setTextColor(
            ContextCompat.getColor(
                mContext,
                if (selected) R.color.colorAccent else R.color.dark_gray
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