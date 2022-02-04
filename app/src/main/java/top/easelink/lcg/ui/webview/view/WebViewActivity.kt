package top.easelink.lcg.ui.webview.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.webkit.*
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ShareCompat
import com.airbnb.lottie.LottieAnimationView
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import timber.log.Timber
import top.easelink.framework.customview.webview.HorizontalScrollDisableWebView
import top.easelink.framework.threadpool.CalcPool
import top.easelink.framework.threadpool.IOPool
import top.easelink.framework.threadpool.Main
import top.easelink.lcg.R
import top.easelink.lcg.account.AccountManager.isLoggedIn
import top.easelink.lcg.account.UserDataRepo.updateUserInfo
import top.easelink.lcg.appinit.LCGApp
import top.easelink.lcg.event.EVENT_SHARE_ARTICLE_URL
import top.easelink.lcg.event.sendSingleEvent
import top.easelink.lcg.service.web.HookInterface
import top.easelink.lcg.ui.main.me.source.UserInfoRepo.requestUserInfo
import top.easelink.lcg.ui.main.view.MainActivity
import top.easelink.lcg.ui.webview.FORCE_ENABLE_JS_KEY
import top.easelink.lcg.ui.webview.OPEN_LOGIN_PAGE
import top.easelink.lcg.ui.webview.TITLE_KEY
import top.easelink.lcg.utils.WebsiteConstant.EXTRA_TABLE_HTML
import top.easelink.lcg.utils.WebsiteConstant.LOGIN_QUERY
import top.easelink.lcg.utils.WebsiteConstant.QQ_LOGIN_URL
import top.easelink.lcg.utils.WebsiteConstant.SERVER_BASE_URL
import top.easelink.lcg.utils.WebsiteConstant.URL_KEY
import top.easelink.lcg.utils.showMessage
import top.easelink.lcg.utils.updateCookies
import kotlin.coroutines.CoroutineContext

class WebViewActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var mWebView: WebView
    private lateinit var animationView: LottieAnimationView
    private lateinit var videoLayout: FrameLayout

    private var mUrl: String? = null
    private var mHtml: String? = null
    private var mForceEnableJs = true
    private var isOpenLoginEvent = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initContentView()
        initActionBar()
        initWebView()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_VIEW == intent.action && intent.data != null) {
            val url = intent.data.toString()
            mWebView.loadUrl(url)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // safe remove webview refers : https://stackoverflow.com/questions/5267639/how-to-safely-turn-webview-zooming-on-and-off-as-needed
        (mWebView.parent as? ViewGroup)?.removeView(mWebView)
        mWebView.destroy()
    }

    private fun initContentView() {
        setContentView(R.layout.activity_web_view)
        mWebView = findViewById(R.id.web_view)
        animationView = findViewById(R.id.searching_file)
        videoLayout = findViewById(R.id.container)
    }

    private fun openInSystemBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun initWebView() {
        mWebView.webViewClient = webViewClient
        mWebView.webChromeClient = InnerChromeClient()
        mWebView.setDownloadListener { url: String, _: String?, _: String?, _: String?, _: Long ->
            openInSystemBrowser(url)
        }
        val intent = intent
        // load url from intent data
        isOpenLoginEvent = getIntent().getBooleanExtra(OPEN_LOGIN_PAGE, false)
        mForceEnableJs = intent.getBooleanExtra(FORCE_ENABLE_JS_KEY, false)
        if (!TextUtils.isEmpty(mUrl)) {
            updateWebViewSettingsRemote()
            if (isOpenLoginEvent) {
                mWebView.removeJavascriptInterface(HOOK_NAME)
                mWebView.addJavascriptInterface(WebViewHook(), HOOK_NAME)
            }
            mWebView.loadUrl(mUrl)
        } else if (!TextUtils.isEmpty(mHtml)) {
            updateWebViewSettingsLocal()
            mWebView.loadDataWithBaseURL("", mHtml, "text/html", "UTF-8", "")
        }
    }

    inner class WebViewHook: HookInterface {
        @JavascriptInterface
        override fun processHtml(html: String?) {
            if (html.isNullOrBlank()) return
            launch(CalcPool) {
                val doc = Jsoup.parse(html)
                doc.selectFirst("div.avt") ?: return@launch
                isLoggedIn.postValue(true)
                doc.getElementById("messagetext")?.text()?.let {
                    showMessage(it)
                } ?: showMessage(R.string.login_successfully)
                delay(1000)
                withContext(IOPool) {
                    requestUserInfo()?.let(::updateUserInfo)
                }
                launch(Main) {
                    startActivity(Intent(mWebView.context, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    private fun initData() {
        val intent = intent
        val uri = intent.data
        if (uri != null && uri.scheme == "lcg") {
            mUrl = uri.toString().replace("lcg:", SERVER_BASE_URL)
            return
        }
        mUrl = intent.getStringExtra(URL_KEY)
        if (TextUtils.isEmpty(mUrl) && uri != null) {
            mUrl = uri.toString()
        }
        mHtml = intent.getStringExtra(EXTRA_TABLE_HTML)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menu.clear()
        menuInflater.inflate(R.menu.webview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val drawable = item.icon
        if (drawable is Animatable) {
            (drawable as Animatable).start()
        }
        when (item.itemId) {
            R.id.action_share -> {
                sendSingleEvent(EVENT_SHARE_ARTICLE_URL)
                val shareIntent = shareIntent
                startActivity(shareIntent)
                return true
            }
            R.id.action_open_in_webview -> {
                val url = mWebView.url
                if (url != null) {
                    openInSystemBrowser(url)
                } else {
                    showMessage(R.string.general_error)
                }
                return true
            }
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val shareIntent: Intent
        get() = ShareCompat.IntentBuilder.from(this)
            .setText(getString(R.string.share_template, mWebView.title, mWebView.url))
            .setSubject(mWebView.title)
            .setChooserTitle(getString(R.string.share_title))
            .setType("text/plain")
            .createChooserIntent()

    override fun onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack()
        } else {
            finish()
        }
    }

    private fun initActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.web_view_toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            val title = intent.getIntExtra(TITLE_KEY, 0)
            if (title != 0) {
                supportActionBar!!.setTitle(title)
            }
        }
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        when (config.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            }
        }
    }

    private val webViewClient: WebViewClient
        get() = InnerWebViewClient()

    private fun setLoading(isLoading: Boolean) {
        animationView.visibility = if (isLoading) View.VISIBLE else View.GONE
        mWebView.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun updateWebViewSettingsLocal() {
        val settings = mWebView.settings
        if (mWebView is HorizontalScrollDisableWebView) {
            (mWebView as HorizontalScrollDisableWebView).setScrollEnable(true)
        }
        settings.javaScriptEnabled = mForceEnableJs
        // Zoom Setting
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        settings.displayZoomControls = false
        settings.blockNetworkImage = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.defaultTextEncodingName = "UTF-8"
        settings.builtInZoomControls = true
        settings.setAppCacheEnabled(true)
        settings.setSupportZoom(true)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun updateWebViewSettingsRemote() {
        val settings = mWebView.settings
        settings.javaScriptEnabled = mForceEnableJs
        settings.domStorageEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.defaultTextEncodingName = "UTF-8"
        settings.builtInZoomControls = false
        settings.setAppCacheEnabled(true)
        settings.setSupportZoom(false)
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        settings.blockNetworkImage = true
    }

    private inner class InnerChromeClient : WebChromeClient() {
        private var mCustomViewCallback: CustomViewCallback? = null

        //  横屏时，显示视频的view
        private var mCustomView: View? = null

        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            super.onShowCustomView(view, callback)
            //如果view 已经存在，则隐藏
            if (mCustomView != null) {
                callback.onCustomViewHidden()
                return
            }
            mCustomView = view
            view.visibility = View.VISIBLE
            mCustomViewCallback = callback
            videoLayout.addView(view)
            videoLayout.bringToFront()

            //设置横屏
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        override fun onHideCustomView() {
            super.onHideCustomView()
            if (mCustomView == null) return
            mCustomView?.visibility = View.GONE
            videoLayout.removeView(mCustomView)
            mCustomView = null
            try {
                mCustomViewCallback?.onCustomViewHidden()
            } catch (ignored: Exception) {
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT //竖屏
        }
    }

    private inner class InnerWebViewClient : WebViewClient() {
        override fun onPageCommitVisible(view: WebView, url: String) {
            super.onPageCommitVisible(view, url)
            setLoading(false)
            CookieManager.getInstance().getCookie(url)?.let {
                updateCookies(it, isOpenLoginEvent)
            }
            if (isOpenLoginEvent) {
                view.loadUrl("javascript:$HOOK_NAME.processHtml(document.documentElement.outerHTML);")
            }
            view.settings.blockNetworkImage = false
        }

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            view.settings.blockNetworkImage = true
            setLoading(true)
        }

        override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
            handler.proceed()
        }

        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val url = request.url.toString()
            when {
                TextUtils.isEmpty(url) -> return false
                url.startsWith("wtloginmqq://ptlogin/qlogin") -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    return true
                }
                url.startsWith("bdnetdisk") -> {
                    showMessage(R.string.baidu_net_disk_not_support)
                }
            }
            return false
        }
    }

    companion object {
        private const val HOOK_NAME = "hook"
        fun startWebViewWith(url: String, context: Context?) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(URL_KEY, url)
            intent.putExtra(FORCE_ENABLE_JS_KEY, true)
            val c = context ?: LCGApp.context
            c.startActivity(intent)
        }

        fun startWebViewWithHtml(html: String, context: Context) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(EXTRA_TABLE_HTML, html)
            context.startActivity(intent)
        }

        fun openLoginPage(context: Context) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(URL_KEY, SERVER_BASE_URL + LOGIN_QUERY)
            intent.putExtra(FORCE_ENABLE_JS_KEY, true)
            intent.putExtra(OPEN_LOGIN_PAGE, true)
            context.startActivity(intent)
        }

        fun openQQLoginPage(context: Context) {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(URL_KEY, QQ_LOGIN_URL)
            intent.putExtra(FORCE_ENABLE_JS_KEY, true)
            intent.putExtra(OPEN_LOGIN_PAGE, true)
            context.startActivity(intent)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable)
        }
}