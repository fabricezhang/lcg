package top.easelink.lcg.ui.main.discover.view

import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder

abstract class BaseNavigationBinder<T, VH : RecyclerView.ViewHolder> : ItemViewBinder<T, VH>()