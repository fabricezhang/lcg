package top.easelink.lcg.ui.profile.view

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import top.easelink.lcg.R
import top.easelink.lcg.ui.profile.model.ExtraUserInfo

class UserInfoGridViewAdapter internal constructor(
    context: Context,
    @field:LayoutRes @param:LayoutRes private val mLayoutRes: Int
) : ArrayAdapter<ExtraUserInfo>(context, mLayoutRes) {

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {
        val v: View
        val vh = if (convertView == null) {
            (context as Activity).layoutInflater.inflate(mLayoutRes, parent, false).let {
                ViewHolder().apply {
                    titleText = it.findViewById(R.id.title_tv)
                    valueText = it.findViewById(R.id.value_tv)
                    it.tag = this
                    v = it
                }
            }
        } else {
            v = convertView
            convertView.tag as ViewHolder
        }
        getItem(position)?.let {
            vh.titleText?.text = it.title
            vh.valueText?.text = it.content
        }
        return v
    }


    private class ViewHolder {
        var titleText: TextView? = null
        var valueText: TextView? = null
    }

}