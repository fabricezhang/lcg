package top.easelink.lcg.ui.main.article.viewmodel;

import android.view.View;

import androidx.annotation.NonNull;

import top.easelink.framework.customview.htmltextview.ClickableSpecialSpan;
import top.easelink.lcg.ui.webview.view.WebViewActivity;

class ClickableSpecialSpanImpl extends ClickableSpecialSpan {

    public ClickableSpecialSpanImpl() {}

    private ClickableSpecialSpanImpl(String html) {
        this.html = html;
    }

    @Override
    public ClickableSpecialSpan newInstance() {
        return new ClickableSpecialSpanImpl(getHtml());
    }

    @Override
    public void onClick(@NonNull View view) {
        WebViewActivity.startWebViewWithHtml(html, view.getContext());
    }
}