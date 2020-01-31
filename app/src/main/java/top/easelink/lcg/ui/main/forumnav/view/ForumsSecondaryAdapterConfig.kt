package top.easelink.lcg.ui.main.forumnav.view

import android.content.Context
import android.view.View
import android.widget.TextView
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryFooterViewHolder
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryHeaderViewHolder
import com.kunminx.linkage.adapter.viewholder.LinkageSecondaryViewHolder
import com.kunminx.linkage.bean.BaseGroupedItem
import com.kunminx.linkage.bean.DefaultGroupedItem
import com.kunminx.linkage.contract.ILinkageSecondaryAdapterConfig
import kotlinx.android.synthetic.main.item_forum_secondary_header.view.*
import kotlinx.android.synthetic.main.item_forum_secondary_linear.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.model.OpenForumEvent

internal class ForumsSecondaryAdapterConfig : 
    ILinkageSecondaryAdapterConfig<DefaultGroupedItem.ItemInfo> {
    private lateinit var mContext: Context
    override fun setContext(context: Context) {
        mContext = context
    }

    override fun getGridLayoutId(): Int {
        return 0
    }

    override fun getLinearLayoutId(): Int {
        return R.layout.item_forum_secondary_linear
    }

    override fun getHeaderLayoutId(): Int {
        return R.layout.item_forum_secondary_header
    }

    override fun getFooterLayoutId(): Int {
        return R.layout.item_forum_secondary_footer
    }

    override fun getHeaderTextViewId(): Int {
        return com.kunminx.linkage.R.id.secondary_header
    }

    override fun getSpanCountOfGridMode(): Int {
        return 0
    }

    override fun onBindViewHolder(
        holder: LinkageSecondaryViewHolder,
        item: BaseGroupedItem<DefaultGroupedItem.ItemInfo>
    ) {
        holder.itemView.secondary_title.apply {
            text = item.info.title
            setOnClickListener {
                EventBus.getDefault().post(OpenForumEvent(item.info.title, item.info.content))
            }
        }
    }

    override fun onBindHeaderViewHolder(
        holder: LinkageSecondaryHeaderViewHolder,
        item: BaseGroupedItem<DefaultGroupedItem.ItemInfo>
    ) {
        holder.itemView.secondary_header.text = item.header
    }

    override fun onBindFooterViewHolder(
        holder: LinkageSecondaryFooterViewHolder,
        item: BaseGroupedItem<DefaultGroupedItem.ItemInfo>
    ) {
    }
}