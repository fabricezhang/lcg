package top.easelink.lcg.service.web;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import top.easelink.lcg.LCGApp;

/**
 * author : junzhang
 * date   : 2019-07-23 13:58
 * desc   :
 */
@SuppressLint({"SetJavaScriptEnabled", "StaticFieldLeak"})
public class WebViewWrapper {

    private static final String HOOK_NAME = "hook";
    private static WebViewWrapper instance;
    private WebView mWebView;

    private WebViewWrapper() {
        mWebView = new WebView(LCGApp.getContext());
        mWebView.setWebViewClient(new InnerWebViewClient());
        updateWebViewSettings();
    }

    public static void init() {
        instance = getInstance();
    }

    public void loadUrl(String url, HookInterface hookInterface) {
        mWebView.removeJavascriptInterface(HOOK_NAME);
        mWebView.addJavascriptInterface(hookInterface, HOOK_NAME);
        mWebView.loadUrl(url);
    }
    public static WebViewWrapper getInstance() {
        if (instance == null) {
            synchronized (WebViewWrapper.class) {
                if (instance == null) {
                    instance = new WebViewWrapper();
                }
            }
        }
        return instance;
    }

    private void updateWebViewSettings() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setBuiltInZoomControls(false);
        settings.setAppCacheEnabled(true);
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }

    private class InnerWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
//            CookieManager cookieManager = CookieManager.getInstance();
//            String cookieUrl = cookieManager.getCookie(url);
//            Timber.d("Cookie : %s", cookieUrl);
            view.loadUrl("javascript:" + HOOK_NAME + ".processHtml(document.documentElement.outerHTML);");
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
