package top.easelink.framework.customview.htmltextview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

public class HtmlGlideImageGetter implements Html.ImageGetter {

    private Context context;
    private TextView textView;

    public HtmlGlideImageGetter(Context context, TextView target) {
        this.context = context;
        textView = target;
    }

    @Override
    public Drawable getDrawable(String url) {
        BitmapDrawablePlaceholder drawable = new BitmapDrawablePlaceholder();

        Glide.with(context)
                .asDrawable()
                .load(url)
                .fitCenter()
                .into(drawable);

        return drawable;
    }

    private class BitmapDrawablePlaceholder extends BitmapDrawable implements Target<Drawable> {

        private Drawable drawable;

        BitmapDrawablePlaceholder() {
            super(context.getResources(), Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));
        }

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }


        private void setDrawable(Drawable drawable) {
            this.drawable = drawable;
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            int maxWidth = textView.getMeasuredWidth();
            int calculatedHeight = maxWidth * drawableHeight / drawableWidth;
            drawable.setBounds(0, 0, maxWidth, calculatedHeight);
            setBounds(0, 0, maxWidth, calculatedHeight);

            textView.setText(textView.getText());
        }

        @Override
        public void onLoadStarted(@Nullable Drawable placeholderDrawable) {
            if(placeholderDrawable != null) {
                setDrawable(placeholderDrawable);
            }
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            if (errorDrawable != null) {
                setDrawable(errorDrawable);
            }
        }

        @Override
        public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
            setDrawable(drawable);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholderDrawable) {
            if(placeholderDrawable != null) {
                setDrawable(placeholderDrawable);
            }
        }

        @Override
        public void getSize(@NonNull SizeReadyCallback cb) {
            textView.post(() -> cb.onSizeReady(textView.getWidth(), SIZE_ORIGINAL));
        }

        @Override
        public void removeCallback(@NonNull SizeReadyCallback cb) {}

        @Override
        public void setRequest(@Nullable Request request) {}

        @Nullable
        @Override
        public Request getRequest() {
            return null;
        }

        @Override
        public void onStart() {}

        @Override
        public void onStop() {}

        @Override
        public void onDestroy() {}

    }
}