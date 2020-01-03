package top.easelink.framework.topbase

import android.content.Context
import androidx.fragment.app.Fragment
import top.easelink.framework.topbase.ControllableFragment.TAG_PREFIX

abstract class TopFragment : Fragment() {

    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context.also {
            if (this is ControllableFragment && this.isControllable) {
                (it as? Callback)?.onFragmentAttached(TAG_PREFIX + this.javaClass.simpleName)
            }
        }
    }

    interface Callback {
        fun onFragmentAttached(tag: String)
        fun onFragmentDetached(tag: String): Boolean
    }
}