package top.easelink.framework.customview.webview;

import android.content.Context;
import android.util.AttributeSet;

public class HorizontalScrollDisableWebView extends LollipopFixedWebView {

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