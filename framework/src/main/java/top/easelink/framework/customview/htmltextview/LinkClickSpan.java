package top.easelink.framework.customview.htmltextview;

import android.content.Context;
import android.text.style.ClickableSpan;
import android.view.View;

public class LinkClickSpan extends ClickableSpan {
    private Context context;
    private OnLinkTagClickListener listener;
    private String url;

    public LinkClickSpan(Context context, String url) {
        this.context = context;
        this.url = url;
    }

    public void onClick(View paramView) {
        if (this.listener != null) {
            this.listener.onLinkClick(this.context, this.url);
        }
    }

    public void setListener(OnLinkTagClickListener onLinkTagClickListener)
    {
        this.listener = onLinkTagClickListener;
    }
}
