package top.easelink.lcg.ui.webview.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;
import top.easelink.lcg.R;

import static top.easelink.lcg.ui.webview.WebViewConstants.*;

public class WebViewActivity extends AppCompatActivity {

    private boolean mForceEnableJs = false;

    protected String mUrl;

    private WebView mWebView;
    private ProgressBar mProgressBar;

    public static void startWebViewWith(String url, Activity activity) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(URL_KEY, url);
        intent.putExtra(FORCE_ENABLE_JS_KEY, true);
        activity.startActivity(intent);
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
        mProgressBar = findViewById(R.id.loading_progress_bar);
    }

    protected void initWebView() {
        mWebView.setWebViewClient(getWebViewClient());
        initUrl();
        Intent intent = getIntent();
        mForceEnableJs = intent.getBooleanExtra(FORCE_ENABLE_JS_KEY, false);
        updateWebViewSettings();
        mWebView.loadUrl(mUrl);
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
        getMenuInflater().inflate(R.menu.main, menu);
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
                break;
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
        mProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private class InnerWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
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
