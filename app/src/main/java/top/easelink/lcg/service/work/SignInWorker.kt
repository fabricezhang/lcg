package top.easelink.lcg.service.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber
import top.easelink.lcg.BuildConfig
import top.easelink.lcg.network.Client
import java.util.concurrent.TimeUnit

class SignInWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val alertInfo = Client.sendGetRequestWithUrl(SIGN_IN_URL)
            .getElementsByClass("alert_info")
            ?.first()
            ?.selectFirst("p")
            ?.text()
        Timber.d(alertInfo)
        return Result.success()
    }

    companion object {

        val WORK_INTERVAL: Long = if (BuildConfig.DEBUG) 60L else 8L
        val DEFAULT_TIME_UNIT = if (BuildConfig.DEBUG) TimeUnit.MINUTES else TimeUnit.HOURS

        private const val SIGN_IN_URL = "https://www.52pojie.cn/home.php?mod=task&do=apply&id=2"
    }
}
