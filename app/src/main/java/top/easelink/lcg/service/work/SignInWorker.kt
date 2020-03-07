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

        private const val SIGN_IN_URL = "https://www.52pojie.cn/home.php?mod=task&do=apply&id=2"
        const val TAG = "SignInWorker"

        fun sendSignInRequest() {
            val alertInfo = Client.sendGetRequestWithUrl(SIGN_IN_URL)
                .getElementsByClass("alert_info")
                ?.first()
                ?.selectFirst("p")
                ?.text()
            Timber.d(alertInfo)
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
