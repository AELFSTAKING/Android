package io.tick.base.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.ImmersionBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.tick.base.LoadingDialog;
import io.tick.base.appupdate.AppUpdateUtils;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.net.BaseUrl;
import io.tick.base.util.ToastUtils;

public abstract class BaseActivity extends AppCompatActivity implements IView {

    private Unbinder unbinder;

    protected LoadingDialog loadingDialog;

    protected abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initEvents();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (enabledEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
            unbinder = ButterKnife.bind(this);
            //初始化沉浸式
            if (isImmersionBarEnabled()) {
                initImmersionBar();
            }
            initViews(savedInstanceState);
            initEvents();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCheckAppUpdate()) {
            AppUpdateUtils.checkUpdate(this, BaseUrl.UPDATE_URL);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (unbinder != Unbinder.EMPTY) {
            unbinder.unbind();
            this.unbinder = null;
        }
    }

    protected boolean isImmersionBarEnabled() {
        return true;
    }

    protected void initImmersionBar() {
        ImmersionBar.with(this)
                .keyboardEnable(true)
                .statusBarDarkFont(true, 0.2f)
                .init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//, priority = 100
    public final void onEventCenter(EventBusCenter event) {
        if (null != event) {
            onEventCallback(event);
        }
    }

    /**
     * 根据code区分当前事件类型
     */
    protected void onEventCallback(EventBusCenter event) {

    }

    /**
     * 注册event bus
     */
    protected boolean enabledEventBus() {
        return true;
    }

    @Override
    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setCancelable(false);
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void toast(CharSequence s) {
        ToastUtils.showShortToast(s);
    }

    protected boolean isCheckAppUpdate() {
        return true;
    }
}
