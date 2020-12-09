package top.easelink.framework.topbase

import android.content.Context
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class TopFragment : Fragment(), CoroutineScope by MainScope() {

    lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context.also {
            if (this is ControllableFragment && isControllable()) {
                (it as? Callback)?.onFragmentAttached(this.getBackStackTag())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    interface Callback {
        fun onFragmentAttached(tag: String)
        fun onFragmentDetached(tag: String): Boolean
    }
}