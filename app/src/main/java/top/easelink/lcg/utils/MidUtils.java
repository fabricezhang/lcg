package top.easelink.lcg.utils;

import android.content.Context;

import com.tencent.mid.api.MidCallback;
import com.tencent.mid.api.MidService;

import timber.log.Timber;

public class MidUtils {

    public static void getMid(Context context) {
        MidService.requestMid(context, new MidCallback() {
            @Override
            public void
            onSuccess(Object mid) {
                Timber.d("success to get mid:%s", mid);
            }

            @Override
            public void
            onFail(int errCode, String msg) {
                Timber.d("failed to get mid, errCode:" + errCode + ",msg:" + msg);
            }
        });
    }
}
