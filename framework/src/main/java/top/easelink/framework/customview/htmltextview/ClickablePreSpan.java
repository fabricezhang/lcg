package top.easelink.framework.customview.htmltextview;

import android.view.View;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

class ClickablePreSpan extends ClickablePreCodeSpan {

    @Override
    public ClickablePreCodeSpan newInstance() {
        return new ClickablePreSpan();
    }

    @Override
    public void onClick(@NotNull View view) {
        Timber.d("Test");
    }
}