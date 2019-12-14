package top.easelink.framework.topbase

import android.content.Context
import androidx.fragment.app.Fragment

abstract class TopFragment : Fragment() {

    var topActivity: TopActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TopActivity) {
            topActivity = context
            context.onFragmentAttached(tag?:this::class.java.simpleName)
        }
    }

    override fun onDetach() {
        topActivity = null
        super.onDetach()
    }

    interface Callback {
        fun onFragmentAttached(tag: String)
        fun onFragmentDetached(tag: String): Boolean
    }
}