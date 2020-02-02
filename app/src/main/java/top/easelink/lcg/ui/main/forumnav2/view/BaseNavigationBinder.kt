package top.easelink.lcg.ui.main.forumnav2.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder

abstract class BaseNavigationBinder<T, VH : RecyclerView.ViewHolder>: ItemViewBinder<T, VH>()

class ForumNavigationBinder(): BaseNavigationBinder<ForumNavigationItem, ForumNavigationVH>() {

    override fun onBindViewHolder(holder: ForumNavigationVH, item: ForumNavigationItem) {
        holder.onBind(item, null)
    }

    override fun onBindViewHolder(
        holder: ForumNavigationVH,
        item: ForumNavigationItem,
        payloads: List<Any>
    ) {
        holder.onBind(item, payloads)
    }

    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): ForumNavigationVH {
        return ForumNavigationVH(inflater, parent)
    }
}