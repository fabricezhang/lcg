package top.easelink.framework.base

import android.content.Context
import androidx.fragment.app.Fragment

open class ELFragment : Fragment() {

    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context.also {
            (it as? ELActivity)?.onFragmentAttached(tag?:this.javaClass.simpleName)
        }
    }

    override fun onDetach() {
        super.onDetach()
        (mContext as? ELActivity)?.onFragmentDetached(tag?:this.javaClass.simpleName)
    }

    interface Callback {
        fun onFragmentAttached(tag: String)
        fun onFragmentDetached(tag: String): Boolean
    }
}
