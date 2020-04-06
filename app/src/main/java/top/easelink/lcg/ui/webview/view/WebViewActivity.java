package top.easelink.lcg.ui.webview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import com.airbnb.lottie.LottieAnimationView;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.Objects;

import timber.log.Timber;
import top.easelink.framework.customview.webview.HorizontalScrollDisableWebView;
import top.easelink.lcg.R;
import top.easelink.lcg.appinit.LCGApp;
import top.easelink.lcg.mta.EventHelperKt;
import top.easelink.lcg.service.web.HookInterface;
import top.easelink.lcg.ui.main.view.MainActivity;
import top.easelink.lcg.utils.ToastUtilsKt;

import static top.easelink.lcg.mta.MTAConstantKt.EVENT_SHARE_ARTICLE_URL;
import static top.easelink.lcg.ui.webview.WebViewConstantsKt.FORCE_ENABLE_JS_KEY;
import static top.easelink.lcg.ui.webview.WebViewConstantsKt.OPEN_LOGIN_PAGE;
import static top.easelink.lcg.ui.webview.WebViewConstantsKt.TITLE_KEY;
import static top.easelink.lcg.utils.CookieUtilsKt.setCookies;
import static top.easelink.lcg.utils.WebsiteConstant.EXTRA_TABLE_HTML;
import static top.easelink.lcg.utils.WebsiteConstant.LOGIN_URL;
import static top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL;
import static top.easelink.lcg.utils.WebsiteConstant.URL_KEY;

public class WebViewActivity extends AppCompatActivity {

    private static final String HOOK_NAME = "hook";
    private boolean mForceEnableJs = true;

    private  boolean isOpenLoginEvent = false;
    protected String mUrl;
    protected String mHtml;

    private WebView mWebView;
    private LottieAnimationView animationView;
    private FrameLayout videoLayout;

    public static void startWebViewWith(String url, Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL_KEY, url);
        intent.putExtra(FORCE_ENABLE_JS_KEY, true);
        context = context == null? LCGApp.getContext() : context;
        context.startActivity(intent);
    }

    public static void startWebViewWithHtml(String html, Context context) {
        context = context == null? LCGApp.getContext() : context;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(EXTRA_TABLE_HTML, html);
        context.startActivity(intent);
    }

    public static void openLoginPage(Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL_KEY, SERVER_BASE_URL + LOGIN_URL);
        intent.putExtra(FORCE_ENABLE_JS_KEY, true);
        intent.putExtra(OPEN_LOGIN_PAGE, true);
        context = context == null? LCGApp.getContext() : context;
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initContentView();
        initActionBar();
        initWebView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // safe remove webview refers : https://stackoverflow.com/questions/5267639/how-to-safely-turn-webview-zooming-on-and-off-as-needed
        if (mWebView != null && mWebView.getParent() != null){
            ((ViewGroup)mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView=null;
        }
    }

    protected void initContentView() {
        setContentView(R.layout.activity_web_view);
        mWebView = findViewById(R.id.web_view);
        animationView = findViewById(R.id.searching_file);
        videoLayout = findViewById(R.id.container);
    }

    private void openInSystemBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    protected void initWebView() {
        mWebView.setWebViewClient(getWebViewClient());
        mWebView.setWebChromeClient(new InnerChromeClient());
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength)
                -> openInSystemBrowser(url));
        Intent intent = getIntent();
        // load url from intent data
        isOpenLoginEvent = getIntent().getBooleanExtra(OPEN_LOGIN_PAGE, false);
        mForceEnableJs = intent.getBooleanExtra(FORCE_ENABLE_JS_KEY, false);
        if (!TextUtils.isEmpty(mUrl)) {
            updateWebViewSettingsRemote();
            if (isOpenLoginEvent) {
                mWebView.removeJavascriptInterface(HOOK_NAME);
                mWebView.addJavascriptInterface(new HookInterface() {
                    @Override
                    @JavascriptInterface
                    public void processHtml(String html) {
                        try {
                            Element element = Jsoup.parse(html).selectFirst("div.avt");
                            if (element != null) {
                                mWebView.post(() -> {
                                    mWebView.stopLoading();
                                    try {
                                        // hold on 1.5 seconds to wait for saving user info
                                        Thread.sleep(1500);
                                    } catch (Exception ignored) {
                                    }
                                    startActivity(new Intent(mWebView.getContext(), MainActivity.class));
                                    finish();
                                });
                            }
                        } catch (Exception e) {
                            Timber.w(html);
                        }
                    }
                }, HOOK_NAME);

            }
            mWebView.loadUrl(mUrl);
        } else if(!TextUtils.isEmpty(mHtml)) {
            updateWebViewSettingsLocal();
            mWebView.loadData(mHtml, "text/html", "UTF-8");
        }
    }

    protected void initData() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (uri != null && Objects.equals(uri.getScheme(), "lcg")) {
            mUrl = uri.toString().replace("lcg:", SERVER_BASE_URL);
            return;
        }
        mUrl = intent.getStringExtra(URL_KEY);
        if (TextUtils.isEmpty(mUrl) && uri != null) {
            mUrl = uri.toString();
        }

        mHtml = intent.getStringExtra(EXTRA_TABLE_HTML);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        getMenuInflater().inflate(R.menu.webview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Drawable drawable = item.getIcon();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
        switch (item.getItemId()) {
            case R.id.action_share:
                EventHelperKt.sendEvent(EVENT_SHARE_ARTICLE_URL);
                Intent shareIntent = getShareIntent();
                startActivity(shareIntent);
                return true;
            case R.id.action_open_in_webview:
                openInSystemBrowser(mWebView.getUrl());
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent getShareIntent() {
        return ShareCompat.IntentBuilder.from(this)
                .setText(getString(R.string.share_template, mWebView.getTitle(), mWebView.getUrl()))
                .setSubject(mWebView.getTitle())
                .setChooserTitle(getString(R.string.share_title))
                .setType("text/plain")
                .createChooserIntent();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            finish();
        }
    }

    protected void initActionBar() {
        Toolbar toolbar = findViewById(R.id.web_view_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            int title = getIntent().getIntExtra(TITLE_KEY, 0);
            if (title != 0) {
                getSupportActionBar().setTitle(title);
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NotNull Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    protected WebViewClient getWebViewClient() {
        return new InnerWebViewClient();
    }

    protected void setLoading(boolean isLoading) {
        animationView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mWebView.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private class InnerChromeClient extends WebChromeClient {
        private CustomViewCallback mCustomViewCallback;
        //  横屏时，显示视频的view
        private View mCustomView;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            //如果view 已经存在，则隐藏
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            mCustomView.setVisibility(View.VISIBLE);
            mCustomViewCallback = callback;
            videoLayout.addView(mCustomView);
            videoLayout.bringToFront();

            //设置横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null) {
                return;
            }
            mCustomView.setVisibility(View.GONE);
            videoLayout.removeView(mCustomView);
            mCustomView = null;
            try {
                mCustomViewCallback.onCustomViewHidden();
            } catch (Exception ignored) {
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏
        }
    }

    private class InnerWebViewClient extends WebViewClient {

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            setLoading(false);
            if (isOpenLoginEvent) {
                ToastUtilsKt.showMessage(R.string.login_successfully);
                view.loadUrl("javascript:" + HOOK_NAME + ".processHtml(document.documentElement.outerHTML);");
            }
            setCookies(CookieManager.getInstance().getCookie(url));
            view.getSettings().setBlockNetworkImage(false);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.getSettings().setBlockNetworkImage(true);
            setLoading(true);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (TextUtils.isEmpty(url)) {
                return false;
            } else if (url.startsWith("https://www.52pojie.cn/connect.php?mod=login&op=init&referer=https%3A%2F%2Fwww.52pojie.cn%2F&statfrom=login")){
                ToastUtilsKt.showMessage(R.string.qq_not_support);
                return true;
            } else if (url.startsWith("wtloginmqq://ptlogin/qlogin")) {
                ToastUtilsKt.showMessage(R.string.qq_not_support);
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;
            }
            return false;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void updateWebViewSettingsLocal() {
        WebSettings settings = mWebView.getSettings();
        if (mWebView instanceof HorizontalScrollDisableWebView) {
            ((HorizontalScrollDisableWebView) mWebView).setScrollEnable(true);
        }
        if (mForceEnableJs) {
            settings.setJavaScriptEnabled(true);
        } else {
            settings.setJavaScriptEnabled(false);
        }
        // Zoom Setting
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        settings.setBlockNetworkImage(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setBuiltInZoomControls(true);
        settings.setAppCacheEnabled(true);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void updateWebViewSettingsRemote() {
        WebSettings settings = mWebView.getSettings();
        if (mForceEnableJs) {
            settings.setJavaScriptEnabled(true);
        } else {
            settings.setJavaScriptEnabled(false);
        }
        settings.setDomStorageEnabled(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setBuiltInZoomControls(false);
        settings.setAppCacheEnabled(true);
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setBlockNetworkImage(true);
    }
}
