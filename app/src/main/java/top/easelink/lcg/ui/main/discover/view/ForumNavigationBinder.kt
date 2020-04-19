package top.easelink.lcg.ui.main.discover.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_forums_navigation.view.*
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.discover.model.ForumListModel
import top.easelink.lcg.ui.main.discover.model.ForumNavigationModel

class ForumNavigationBinder : BaseNavigationBinder<ForumListModel, RankListVH>() {

    override fun onBindViewHolder(holder: RankListVH, item: ForumListModel) {
        holder.onBind(item, null)
    }

    override fun onBindViewHolder(
        holder: RankListVH,
        item: ForumListModel,
        payloads: List<Any>
    ) {
        holder.onBind(item, payloads)
    }

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RankListVH {
        return RankListVH(inflater, parent)
    }
}

class ForumNavigationVH(inflater: LayoutInflater, parentView: ViewGroup): BaseNavigationViewHolder(
    inflater.inflate(R.layout.fragment_forums_navigation, parentView, false)
) {
    fun onBind(item: ForumListModel, payloads: List<Any>?) {
        setUp(item.forumList)
    }

    private fun setUp(listModel: List<ForumNavigationModel>) {
        with(itemView) {
            forum_tips.setOnClickListener {
                navigation_grid.smoothScrollToPosition(
                    (navigation_grid.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() + 1
                )
            }
            navigation_grid.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = RecyclerView.HORIZONTAL
                }
                adapter = ForumNavigationAdapter().also {
                    it.addItems(listModel)
                }
            }
        }
    }
}