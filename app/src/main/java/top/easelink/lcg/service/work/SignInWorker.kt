package top.easelink.lcg.service.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jsoup.Jsoup
import timber.log.Timber
import top.easelink.lcg.BuildConfig
import top.easelink.lcg.ui.main.source.local.SharedPreferencesHelper
import java.util.concurrent.TimeUnit

class SignInWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        // do sign in job
        Timber.d("worker start to help you sign in")
        val cookies: Map<String, String> = SharedPreferencesHelper
            .getCookieSp()
            .all
            .mapValues { it.value.toString()}
        val doc = Jsoup.connect(SIGN_IN_URL).cookies(cookies).get()
        val alertInfo = doc?.getElementsByClass("alert_info")
            ?.first()
            ?.selectFirst("p")
            ?.text()
        Timber.d(alertInfo)
        return Result.success()
    }

    companion object {

        val WORK_INTERVAL: Long = if (BuildConfig.DEBUG) 15L else 24L
        val DEFAULT_TIME_UNIT = if (BuildConfig.DEBUG) TimeUnit.MINUTES else TimeUnit.HOURS

        private const val SIGN_IN_URL = "https://www.52pojie.cn/home.php?mod=task&do=apply&id=2"
    }
}
