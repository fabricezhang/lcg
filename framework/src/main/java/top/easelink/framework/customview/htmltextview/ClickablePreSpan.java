package top.easelink.framework.customview.htmltextview;

import android.view.View;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

class ClickablePreSpan extends ClickableSpecialSpan {

    @Override
    public ClickableSpecialSpan newInstance() {
        return new ClickablePreSpan();
    }

    @Override
    public void onClick(@NotNull View view) {
        Timber.d("Test");
    }
}