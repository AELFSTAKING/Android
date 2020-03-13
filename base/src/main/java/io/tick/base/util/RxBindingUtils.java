package io.tick.base.util;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;


public class RxBindingUtils {

    public static void clicks(final Consumer<? super Object> onNext, View... views) {
        for (View v : views) {
            RxView.clicks(v).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(onNext);
        }
    }

    public static void longClicks(final Consumer<? super Object> onNext, View... views) {
        for (View v : views) {
            RxView.longClicks(v).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(onNext);
        }
    }

}
