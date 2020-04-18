package top.easelink.framework.topbase

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import timber.log.Timber

abstract class TopDialog : DialogFragment() {

    lateinit var mContext: Context
    private var lastTryShowTime: Long = 0L

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog { // the content
        val root = RelativeLayout(mContext).also {
            it.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        // creating the fullscreen dialog
        return Dialog(mContext).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setCanceledOnTouchOutside(false)
        }
    }

    override fun show(fragmentManager: FragmentManager, tag: String?) {
        safelyShow(fragmentManager, tag)

    }

    private fun safelyShow(fragmentManager: FragmentManager, tag: String?) {
        //debounce
        val curTime = System.currentTimeMillis()
        if ((curTime - lastTryShowTime) < ViewConfiguration.getDoubleTapTimeout()) {
            lastTryShowTime = curTime
            return
        }
        val clazz = javaClass.superclass as Class<*>
        try {
            val mDismissed = clazz.getDeclaredField("mDismissed")
            mDismissed.isAccessible = true
            mDismissed.set(this, false)
        } catch (e: Exception) {
        }

        try {
            val mShownByMe = clazz.getDeclaredField("mShownByMe")
            mShownByMe.isAccessible = true
            mShownByMe.set(this, true)
        } catch (e: Exception) {
        }

        showCatchException(fragmentManager, tag)
    }

    private fun showCatchException(manager: FragmentManager, tag: String?) {
        if (!isAdded) {
            try {
                manager
                    .beginTransaction()
                    .run {
                        manager.findFragmentByTag(tag)?.let {
                            remove(it)
                        }
                        addToBackStack(null)
                        add(this@TopDialog, tag ?: this::class.java.simpleName)
                        commitAllowingStateLoss()
                    }
            } catch (e: Exception) {
                Timber.e(e)
            }

        }
    }

    protected fun dismissDialog() {
        dismissAllowingStateLoss()
    }

}