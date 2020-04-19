package top.easelink.lcg.ui.main.discover.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_forums_navigation.view.*
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.discover.model.RankListModel
import top.easelink.lcg.ui.main.discover.model.RankModel

class RankListBinder : BaseNavigationBinder<RankListModel, RankListVH>() {

    override fun onBindViewHolder(holder: RankListVH, item: RankListModel) {
        holder.onBind(item, null)
    }

    override fun onBindViewHolder(
        holder: RankListVH,
        item: RankListModel,
        payloads: List<Any>
    ) {
        holder.onBind(item, payloads)
    }

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): RankListVH {
        return RankListVH(
            inflater,
            parent
        )
    }
}

class RankListVH(inflater: LayoutInflater, parentView: ViewGroup): BaseNavigationViewHolder(
    inflater.inflate(R.layout.fragment_forums_navigation, parentView, false)
) {
    fun onBind(item: RankListModel, payloads: List<Any>?) {
        setUp(item.listModel)
    }

    private fun setUp(rankModel: List<RankModel>) {
        with(itemView) {
            forum_tips.setOnClickListener {
                navigation_grid.smoothScrollToPosition(
                    (navigation_grid.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() + 1
                )
            }
            navigation_grid.apply {
                layoutManager = LinearLayoutManager(context).apply {
                    orientation = RecyclerView.VERTICAL
                }
                adapter = RankListAdapter()
                    .also {
                    it.addItems(rankModel)
                }
            }
        }
    }
}