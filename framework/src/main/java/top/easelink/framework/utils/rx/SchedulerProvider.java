package top.easelink.framework.utils.rx;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SchedulerProvider {

    private static SchedulerProvider mInstance;

    public static SchedulerProvider getInstance() {
        if (mInstance == null) {
            synchronized (SchedulerProvider.class) {
                if (mInstance == null) {
                    mInstance = new SchedulerProvider();
                }
            }
        }
        return mInstance;
    }

    public Scheduler computation() {
        return Schedulers.computation();
    }

    public Scheduler io() {
        return Schedulers.io();
    }

    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
