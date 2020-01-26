package top.easelink.framework.topbase

import android.content.Context
import androidx.fragment.app.Fragment

abstract class TopFragment : Fragment() {

    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context.also {
            if (this is ControllableFragment && isControllable()) {
                (it as? Callback)?.onFragmentAttached(this.getBackStackTag())
            }
        }
    }

    interface Callback {
        fun onFragmentAttached(tag: String)
        fun onFragmentDetached(tag: String): Boolean
    }
}