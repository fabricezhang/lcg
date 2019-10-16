package top.easelink.lcg.ui.webview.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;
import top.easelink.lcg.R;
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper;

import static top.easelink.lcg.ui.webview.WebViewConstants.FORCE_ENABLE_JS_KEY;
import static top.easelink.lcg.ui.webview.WebViewConstants.TITLE_KEY;
import static top.easelink.lcg.utils.WebsiteConstant.URL_KEY;

public class WebViewActivity extends AppCompatActivity {

    private boolean mForceEnableJs = true;

    protected String mUrl;

    private WebView mWebView;
    private LottieAnimationView animationView;

    public static void startWebViewWith(String url, Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL_KEY, url);
        intent.putExtra(FORCE_ENABLE_JS_KEY, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initContentView();
        initActionBar();
        initWebView();
    }

    protected void initContentView() {
        setContentView(R.layout.activity_web_view);
        mWebView = findViewById(R.id.web_view);
        animationView = findViewById(R.id.searching_file);
    }

    protected void initWebView() {
        mWebView.setWebViewClient(getWebViewClient());
        initUrl();
        updateWebViewSettings();
        Intent intent = getIntent();
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            mWebView.loadUrl(intent.getData().toString());
        } else {
            mForceEnableJs = intent.getBooleanExtra(FORCE_ENABLE_JS_KEY, false);
            mWebView.loadUrl(mUrl);
        }
    }

    protected void initUrl() {
        Intent intent = getIntent();
        Uri uri = intent.getData();
        mUrl = intent.getStringExtra(URL_KEY);
        if (TextUtils.isEmpty(mUrl) && uri != null) {
            mUrl = uri.toString();
        }
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
                Intent shareIntent = getShareIntent();
                startActivity(shareIntent);
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

    protected WebViewClient getWebViewClient() {
        return new InnerWebViewClient();
    }

    protected void setLoading(boolean isLoading) {
        animationView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mWebView.setVisibility(isLoading ? View.INVISIBLE : View.VISIBLE);
    }

    private class InnerWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            CookieManager cookieManager = CookieManager.getInstance();
            String cookieUrl = cookieManager.getCookie(url);
            Timber.i("Cookie : %s", cookieUrl);
            if (!TextUtils.isEmpty(cookieUrl)) {
                String[] cookies = cookieUrl.split(";");
                List<SharedPreferencesHelper.SpItem> itemList = new ArrayList<>();
                for (String cookie: cookies) {
                    String[] cookiePair = cookie.split("=", 2);
                    itemList.add(new SharedPreferencesHelper.SpItem<>(cookiePair[0], cookiePair[1]));
                    Timber.i("%s = %s", cookiePair[0], cookiePair[1]);
                }
                SharedPreferencesHelper.setPreferenceWithList(SharedPreferencesHelper.getCookieSp(), itemList);
            }
            setLoading(false);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            setLoading(true);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (TextUtils.isEmpty(url)) return false;
            if (url.startsWith("wtloginmqq://ptlogin/qlogin")) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                Timber.e(url);
                return true;
            }
            return false;
//            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void updateWebViewSettings() {
        WebSettings settings = mWebView.getSettings();
        if (mForceEnableJs) {
            settings.setJavaScriptEnabled(true);
        } else {
            settings.setJavaScriptEnabled(false);
        }
        settings.setBlockNetworkImage(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setBuiltInZoomControls(false);
        settings.setAppCacheEnabled(true);
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
    }
}
