package top.easelink.framework.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class HorizontalScrollDisableWebView extends WebView {

    private boolean scrollEnable = false;

    public HorizontalScrollDisableWebView(Context context) {
        super(context);
    }

    public HorizontalScrollDisableWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollDisableWebView(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!scrollEnable) {
            scrollTo(0,t);
        }
    }

    public void setScrollEnable(boolean scrollEnable) {
        this.scrollEnable = scrollEnable;
    }
}  