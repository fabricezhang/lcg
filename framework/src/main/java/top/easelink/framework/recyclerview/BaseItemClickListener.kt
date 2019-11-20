package top.easelink.framework.recyclerview

import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SimpleOnItemTouchListener

@Suppress("unused", "RedundantVisibilityModifier")
open class BaseItemClickListener(private val mListener: OnItemClickListener) : SimpleOnItemTouchListener() {

    private var mGestureDetector: GestureDetectorCompat? = null

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (mGestureDetector == null) {
            useDefaultGestureDetector(rv)
        }
        return mGestureDetector!!.onTouchEvent(e)
    }

    public fun setGestureDetector(gestureDetectorCompat: GestureDetectorCompat) {
        mGestureDetector = gestureDetectorCompat
    }

    private fun useDefaultGestureDetector(recyclerView: RecyclerView) {
        mGestureDetector = GestureDetectorCompat(recyclerView.context, object : SimpleOnGestureListener() {

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                recyclerView.findChildViewUnder(e.x, e.y)?.let {
                    mListener.onItemClick(
                        it,
                        recyclerView.getChildLayoutPosition(it)
                    )
                    return true
                }
                return false
            }

            override fun onLongPress(e: MotionEvent) {
                recyclerView.findChildViewUnder(e.x, e.y)?.let {
                    mListener.onItemLongClick(
                        it,
                        recyclerView.getChildLayoutPosition(it)
                    )
                }
            }

            override fun onDoubleTapEvent(e: MotionEvent): Boolean {
                val action = e.action
                if (action == MotionEvent.ACTION_UP) {
                    recyclerView.findChildViewUnder(e.x, e.y)?.let {
                        mListener.onItemDoubleClick(
                            it,
                            recyclerView.getChildLayoutPosition(it)
                        )
                        return true
                    }
                }
                return false
            }
        })
    }

}