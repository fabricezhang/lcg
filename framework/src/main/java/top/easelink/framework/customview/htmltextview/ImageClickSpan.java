package top.easelink.framework.customview.htmltextview;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import org.jetbrains.annotations.NotNull;

public class ImageClickSpan extends ClickableSpan {
    private Context context;
    private String imageUrl;
    private OnImgTagClickListener listener;
    private int position;

    public ImageClickSpan(Context context, String imageUrl, int position) {
        this.context = context;
        this.imageUrl = imageUrl;
        this.position = position;
    }

    @Override
    public void onClick(@NotNull View v) {
        if (this.listener != null) {
            this.listener.onImageClick(this.context, this.imageUrl, this.position);
        }
    }

    public void setListener(OnImgTagClickListener onTagClickListener) {
        this.listener = onTagClickListener;
    }

    @Override
    public void updateDrawState(TextPaint textPaint) {
        textPaint.setColor(textPaint.linkColor);
        textPaint.setUnderlineText(false);
    }
}