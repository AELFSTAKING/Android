package io.alf.exchange.util;

import android.os.Looper;

public class HandlerHelper {
    private volatile static android.os.Handler mHandler = null;

    public static android.os.Handler handler() {
        if (null == mHandler) {
            synchronized (HandlerHelper.class) {
                if (null == mHandler) {
                    mHandler = new android.os.Handler(Looper.getMainLooper());
                }
            }
        }
        return mHandler;
    }

    public static void clear() {
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
