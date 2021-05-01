package top.easelink.framework.topbase

import android.content.Context
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import timber.log.Timber
import top.easelink.framework.threadpool.Main
import kotlin.coroutines.CoroutineContext

abstract class TopFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Main + CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable)
    }

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