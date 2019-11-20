package top.easelink.framework.recyclerview

import android.view.View
import timber.log.Timber

open class SimpleOnItemClickListener : OnItemClickListener {

    override fun onItemClick(view: View, position: Int) {
        Timber.d("onItemClick pos=$position")
    }

    override fun onItemLongClick(view: View, position: Int) {
        Timber.d("onItemLongClick pos=$position")
    }

    override fun onItemDoubleClick(view: View, position: Int) {
        Timber.d("onItemDoubleClick pos=$position")
    }
}