package top.easelink.lcg.ui.main.discover.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_rank_article_view.view.*
import org.greenrobot.eventbus.EventBus
import top.easelink.framework.base.BaseViewHolder
import top.easelink.lcg.R
import top.easelink.lcg.ui.main.discover.model.RankModel
import top.easelink.lcg.ui.main.discover.source.RankType
import top.easelink.lcg.ui.main.model.OpenArticleEvent


class RankListAdapter : RecyclerView.Adapter<BaseViewHolder>() {
    private val mRankItems: MutableList<RankModel> = mutableListOf()

    override fun getItemCount(): Int {
        return mRankItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_NORMAL
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return RankHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_rank_article_view, parent, false)
        )
    }

    fun addItems(forumModels: List<RankModel>) {
        mRankItems.addAll(forumModels)
        notifyDataSetChanged()
    }

    inner class RankHolder internal constructor(view: View) : BaseViewHolder(view) {
        override fun onBind(position: Int) {
            val model = mRankItems[position]
            itemView.run {
                article_container.setOnClickListener {
                    EventBus.getDefault().post(OpenArticleEvent(model.url))
                }
                val resource = getImageResourceByIndex(model.index)
                if (resource != -1) {
                    index.setImageResource(resource)
                    index.visibility = View.VISIBLE
                } else {
                    index.setImageDrawable(null)
                    index.visibility = View.GONE
                }
                title_text_view.text = model.title
                num.text = getMappingStringByType(num.context, model.type, model.num)
                forum.text = model.forum
            }
        }

        private fun getMappingStringByType(context: Context, type: RankType, num: Int): String {
            val resource = when(type) {
                RankType.VIEW -> R.string.type_view_template
                RankType.REPLY -> R.string.type_reply_template
                RankType.HEAT -> R.string.type_heat_template
                RankType.FAVORITE -> R.string.type_favorite_template
                RankType.SHARE -> R.string.type_share_template
            }
            return context.getString(resource, num)
        }

        private fun getImageResourceByIndex(index: Int): Int {
            return when(index) {
                1 -> R.drawable.ic_first
                2 -> R.drawable.ic_second
                3 -> R.drawable.ic_third
                else -> -1
            }
        }

    }

    companion object {
        const val VIEW_TYPE_NORMAL = 1
    }
}