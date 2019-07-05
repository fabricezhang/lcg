package top.easelink.framework.utils.rx;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SchedulerProvider {

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
