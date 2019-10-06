package top.easelink.lcg.ui.main.me.view;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProviders;

import java.lang.ref.WeakReference;

import javax.inject.Inject;

import timber.log.Timber;
import top.easelink.framework.base.BaseFragment;
import top.easelink.lcg.BR;
import top.easelink.lcg.BuildConfig;
import top.easelink.lcg.R;
import top.easelink.lcg.databinding.FragmentAboutBinding;
import top.easelink.lcg.service.job.SignInJobService;
import top.easelink.lcg.ui.ViewModelProviderFactory;
import top.easelink.lcg.ui.main.me.viewmodel.MeViewModel;

public class MeFragment extends BaseFragment<FragmentAboutBinding, MeViewModel> implements MeNavigator {

    @Inject
    ViewModelProviderFactory factory;

    private ComponentName mServiceComponent;
    private Handler mHandler;
    public static final int MSG_START = 0;
    public static final int MSG_STOP = 1;

    public static final String MESSENGER_INTENT_KEY
            = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";

    public static MeFragment newInstance() {
        Bundle args = new Bundle();
        MeFragment fragment = new MeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_me;
    }

    @Override
    public MeViewModel getViewModel() {
        return ViewModelProviders.of(this, factory).get(MeViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getViewModel().setNavigator(this);
        mServiceComponent = new ComponentName(getBaseActivity(), SignInJobService.class);
        mHandler = new IncomingMessageHandler(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SwitchCompat switchButton = view.findViewById(R.id.auto_sign_in_switch);
        switchButton.setOnClickListener(this::scheduleJob);
    }

    private void scheduleJob(View v) {
        Intent startServiceIntent = new Intent(getBaseActivity(), SignInJobService.class);
        Messenger messengerIncoming = new Messenger(mHandler);
        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming);
        getBaseActivity().startService(startServiceIntent);
        if (((SwitchCompat) v).isChecked()) {
            startJob();
        } else {
            cancelAll();
        }
    }

    private void startJob() {
        JobScheduler jobScheduler = (JobScheduler) getBaseActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            Timber.e("JobScheduler acquire failed");
            return;
        }
        JobInfo.Builder builder = new JobInfo.Builder(0, mServiceComponent)
                .setMinimumLatency(0)
                .setOverrideDeadline(15 * 1000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false);

        jobScheduler.schedule(builder.build());
    }

    private void cancelAll() {
        JobScheduler jobScheduler = (JobScheduler) getBaseActivity().getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (jobScheduler == null) {
            Timber.e("JobScheduler acquire failed");
            return;
        }
        jobScheduler.cancelAll();
    }

    private static class IncomingMessageHandler extends Handler {

        // Prevent possible leaks with a weak reference.
        private WeakReference<MeFragment> mFragment;

        IncomingMessageHandler(MeFragment fragment) {
            super(/* default looper */);
            this.mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            MeFragment meFragment = mFragment.get();
            if (meFragment == null) {
                return;
            }
            switch (msg.what) {
                case MSG_START:
                    break;
                case MSG_STOP:
                    break;
            }
        }

    }
}
