package top.easelink.lcg.service.web;

import android.webkit.JavascriptInterface;

/**
 * author : junzhang
 * date   : 2019-07-24 17:27
 * desc   : Interface should be implemented in order to get the html content
 *          from WebView, to be noticed that @JavascriptInterface should be
 *          marked on the method
 */
public interface HookInterface {

    @JavascriptInterface
    void processHtml(String html);
}
