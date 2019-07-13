package top.easelink.framework.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

public class ScrollToTopBehavior extends CoordinatorLayout.Behavior {

    ScrollToTopBehavior(Context context, AttributeSet attr){
        super(context, attr);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (child.getVisibility() == View.VISIBLE && dy < -100) {
            child.setVisibility(View.INVISIBLE);
        } else if (child.getVisibility() == View.INVISIBLE && dy > 0) {
            child.setVisibility(View.VISIBLE);
        }
    }
}
