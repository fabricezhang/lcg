package top.easelink.lcg.ui.main.article.view;

import android.view.View;

import androidx.annotation.NonNull;

import com.tencent.stat.StatService;

import top.easelink.framework.customview.htmltextview.ClickablePreCodeSpan;
import top.easelink.lcg.ui.webview.view.WebViewActivity;

import static top.easelink.lcg.mta.MTAConstantKt.EVENT_TAP_FOR_CODE;

class ClickablePreCodeSpanImpl extends ClickablePreCodeSpan {

    public ClickablePreCodeSpanImpl() {
    }

    private ClickablePreCodeSpanImpl(String html) {
        this.html = html;
    }

    @Override
    public ClickablePreCodeSpan newInstance() {
        return new ClickablePreCodeSpanImpl(getHtml());
    }

    @Override
    public void onClick(@NonNull View view) {
        StatService.trackCustomEvent(view.getContext(), EVENT_TAP_FOR_CODE);
        WebViewActivity.startWebViewWithHtml(html, view.getContext());
    }
}