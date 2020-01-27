package top.easelink.framework.topbase

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.view.Window
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager

abstract class TopDialog : DialogFragment() {

    lateinit var mContext: Context

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

    override fun show(
        fragmentManager: FragmentManager,
        tag: String?
    ) {
        dialog?.setOnKeyListener {
                _: DialogInterface?, keyCode: Int, event: KeyEvent
            -> keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN
        }
        val transaction = fragmentManager.beginTransaction()
        val prevFragment = fragmentManager.findFragmentByTag(tag)
        if (prevFragment != null) {
            transaction.remove(prevFragment)
        }
        transaction.addToBackStack(null)
        show(transaction, tag)
    }

    protected fun dismissDialog() {
        dismissAllowingStateLoss()
    }

}