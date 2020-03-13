package io.alf.exchange.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.core.content.ContextCompat;

import com.gyf.immersionbar.ImmersionBar;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.alf.exchange.R;
import io.alf.exchange.sample.SampleObserver;
import io.tick.base.mvp.BaseActivity;
import io.tick.base.util.ActivityStartUtils;


public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            toMain();
        }
    }

    @Override
    protected void initEvents() {

    }

    protected void requestPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new SampleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        toMain();
                    }
                });
    }

    private void toMain() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            ActivityStartUtils.jump(SplashActivity.this, MainActivity.class);
            finish();
        }, 1000);
    }

    @Override
    protected boolean isCheckAppUpdate() {
        return false;
    }

    @Override
    protected void initImmersionBar() {
        ImmersionBar.with(this).statusBarView(R.id.top_view)
                .fullScreen(true)
                .addTag("PicAndColor")
                .init();
    }
}
