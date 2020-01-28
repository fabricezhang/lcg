package top.easelink.framework.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Extends {@link SwipeRefreshLayout} to support non-direct descendant scrolling views.
 * <p>
 * {@link SwipeRefreshLayout} works as expected when a scroll view is a direct child: it triggers
 * the refresh only when the view is on top. This class adds a way (@link #setScrollUpChild} to
 * define which view controls this behavior.
 */
public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {

    private View mScrollUpChild;

    public ScrollChildSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollChildSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollUpChild != null) {
            return mScrollUpChild.canScrollVertically(-1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view) {
        mScrollUpChild = view;
    }
}
