package top.easelink.lcg.service.work;

import android.content.Context;
import android.webkit.JavascriptInterface;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;
import top.easelink.lcg.BuildConfig;
import top.easelink.lcg.service.web.HookInterface;
import top.easelink.lcg.service.web.WebViewWrapper;

public class SignInWorker extends Worker {

    public static final long WORK_INTERVAL = 24;
    public static final TimeUnit DEFAULT_TIME_UNIT = BuildConfig.DEBUG ? TimeUnit.SECONDS : TimeUnit.HOURS;

    private static final String SIGN_IN_URL = "https://www.52pojie.cn/home.php?mod=task&do=apply&id=2";

    public SignInWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // do sign in job
        Timber.d("working");
        WebViewWrapper.getInstance().post(SIGN_IN_URL, new HookInterface() {
            @Override
            @JavascriptInterface
            public void processHtml(String html) {
                Document doc = Jsoup.parse(html);
                Timber.d("get html node");
            }
        });
        return Result.success();
    }
}
