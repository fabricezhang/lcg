package top.easelink.lcg.ui.main.forumnav3.view

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import kotlinx.android.synthetic.main.item_forum_secondary_header.view.*
import kotlinx.android.synthetic.main.item_forum_secondary_linear.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.customview.linkagerv.adapter.viewholder.LinkageSecondaryFooterViewHolder
import top.easelink.framework.customview.linkagerv.adapter.viewholder.LinkageSecondaryHeaderViewHolder
import top.easelink.framework.customview.linkagerv.adapter.viewholder.LinkageSecondaryViewHolder
import top.easelink.framework.customview.linkagerv.bean.BaseGroupedItem
import top.easelink.framework.customview.linkagerv.contract.ILinkageSecondaryAdapterConfig
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.forumnav3.model.ChildForumItemInfo
import top.easelink.lcg.ui.main.forumnav3.model.ForumGroupedItem
import top.easelink.lcg.ui.main.model.OpenForumEvent

internal class ForumsSecondaryAdapterConfig :
    ILinkageSecondaryAdapterConfig<ForumGroupedItem.ItemInfo> {
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
        return R.id.secondary_header
    }

    override fun getSpanCountOfGridMode(): Int {
        return 0
    }

    override fun onBindViewHolder(
        holder: LinkageSecondaryViewHolder,
        item: BaseGroupedItem<ForumGroupedItem.ItemInfo>
    ) {
        holder.itemView.apply {
            setOnClickListener {
                EventBus.getDefault().post(OpenForumEvent(item.info.title, item.info.pageUrl, true))
            }
            title.text = item.info.title
            desc.text = item.info.desc

            // bind children items
            bindChildren(children_grid_container, item.info.children)
        }
    }

    private fun bindChildren(gridView: GridView, children: List<ChildForumItemInfo>?) {
        gridView.apply {
            if (children.isNullOrEmpty()) {
                visibility = View.GONE
                return
            } else {
                visibility = View.VISIBLE
            }
            adapter = ChildGridViewAdapter(
                mContext,
                R.layout.item_forums_children_grid
            ).also {
                it.addAll(children)
                it.notifyDataSetChanged()
            }
            children_grid_container.onItemClickListener =
                AdapterView.OnItemClickListener { parent: AdapterView<*>, _: View?, position: Int, _: Long ->
                    (parent.adapter as ChildGridViewAdapter).getItem(position)?.let {
                        EventBus.getDefault().post(
                            OpenForumEvent(
                                it.title.orEmpty(),
                                it.pageUrl,
                                true
                            )
                        )
                    }
                }
        }
    }

    override fun onBindHeaderViewHolder(
        holder: LinkageSecondaryHeaderViewHolder,
        item: BaseGroupedItem<ForumGroupedItem.ItemInfo>
    ) {
        holder.itemView.secondary_header.text = item.header
    }

    override fun onBindFooterViewHolder(
        holder: LinkageSecondaryFooterViewHolder,
        item: BaseGroupedItem<ForumGroupedItem.ItemInfo>
    ) {
    }
}