package top.easelink.lcg.service.work

import android.content.Context
import androidx.work.*
import timber.log.Timber
import top.easelink.lcg.BuildConfig
import top.easelink.lcg.network.Client
import java.util.concurrent.TimeUnit

class SignInWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        try {
            Timber.d("Sign In Job Start")
            sendSignInRequest()
        } catch (e: Exception) {
            Timber.e(e, "Sign In Job Failed")
            return Result.retry()
        }
        return Result.success()
    }

    companion object {

        private val WORK_INTERVAL: Long = if (BuildConfig.DEBUG) 15L else 8L
        private val DEFAULT_TIME_UNIT = if (BuildConfig.DEBUG) TimeUnit.SECONDS else TimeUnit.HOURS

        private const val APPLY_TASK_URL = "https://www.52pojie.cn/home.php?mod=task&do=apply&id=2"
        private const val DRAW_TASK_URL = "https://www.52pojie.cn/home.php?mod=task&do=draw&id=2"
        private const val TASK_APPLIED = "已申请"
        const val TAG = "SignInWorker"

        fun sendSignInRequest() {
            Client.sendGetRequestWithUrl(APPLY_TASK_URL)
                .getElementsByClass("alert_info")
                ?.first()
                ?.selectFirst("p")
                ?.text()
                ?.takeIf {
                    Timber.d(it)
                    it.contains(TASK_APPLIED)
                }?.run {
                    Client.sendGetRequestWithUrl(DRAW_TASK_URL)
                        .getElementsByClass("alert_info")
                        ?.first()
                        ?.selectFirst("p")
                        ?.text()
                        ?.let {
                            Timber.d(it)
                        }
                }
        }

        fun startSignInWork(): Operation {
            val constraints = Constraints.Builder()
                .setRequiresDeviceIdle(false)
                .setRequiresCharging(false)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(false)
                .build()
            val request = PeriodicWorkRequest.Builder(
                    SignInWorker::class.java,
                    WORK_INTERVAL,
                    DEFAULT_TIME_UNIT
                )
                .setConstraints(constraints)
                .addTag(TAG)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 15L, TimeUnit.MINUTES)
                .build()
            return WorkManager.getInstance().enqueueUniquePeriodicWork(
                TAG,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
