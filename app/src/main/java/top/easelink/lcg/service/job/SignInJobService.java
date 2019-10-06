package top.easelink.lcg.service.job;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import androidx.annotation.Nullable;

import timber.log.Timber;

import static top.easelink.lcg.ui.main.me.view.MeFragment.MESSENGER_INTENT_KEY;
import static top.easelink.lcg.ui.main.me.view.MeFragment.MSG_START;
import static top.easelink.lcg.ui.main.me.view.MeFragment.MSG_STOP;


public class SignInJobService extends JobService {

    private Messenger mActivityMessenger;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mActivityMessenger = intent.getParcelableExtra(MESSENGER_INTENT_KEY);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        sendMessage(MSG_START, params.getJobId());

        Timber.i("on start job: " + params.getJobId());
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // Stop tracking these job parameters, as we've 'finished' executing.
        sendMessage(MSG_STOP, params.getJobId());
        Timber.i("on stop job: " + params.getJobId());

        // Return false to drop the job.
        return false;
    }

    private void sendMessage(int messageID, @Nullable Object params) {
        if (mActivityMessenger == null) {
            return;
        }
        Message m = Message.obtain();
        m.what = messageID;
        m.obj = params;
        try {
            mActivityMessenger.send(m);
        } catch (RemoteException e) {
            Timber.e("Error passing service object back to activity.");
        }
    }
}
