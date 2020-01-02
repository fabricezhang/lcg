package top.easelink.framework.base

import android.content.Context
import androidx.fragment.app.Fragment

open class ELFragment : Fragment() {

    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context.also {
            (it as? BaseActivity<*, *>)?.onFragmentAttached(tag)
        }

    }

    override fun onDetach() {
        super.onDetach()
        (mContext as? BaseActivity<*, *>)?.onFragmentDetached(tag)
    }
}
