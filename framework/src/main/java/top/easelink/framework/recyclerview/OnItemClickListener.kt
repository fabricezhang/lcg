package top.easelink.framework.recyclerview

import android.view.View

interface OnItemClickListener {
    fun onItemClick(view: View, position: Int)
    fun onItemLongClick(view: View, position: Int)
    fun onItemDoubleClick(view: View, position: Int)
}