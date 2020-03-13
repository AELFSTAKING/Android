package io.tick.base.mvp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.tick.base.LoadingDialog;
import io.tick.base.R;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.util.ToastUtils;

/**
 * Fragment的可见性可分为三种情况：
 * 情况一、当Fragment嵌套在Fragment中时，调用父Fragment的hide方法进行切换时调用{@link Fragment#onHiddenChanged(boolean)}
 * 情况二、当父容器是ViewPager时，切换ViewPager的item时调用{@link Fragment#setUserVisibleHint(boolean)}
 * 情况三、当Fragment回到屏幕视图顶端时调用{@link Fragment#onResume()}和{@link Fragment#onPause()}
 * <p>
 * 所有的情况都可以直接在{@link BaseDialogFragment {@link #onVisibleToUser()} {@link
 * #onHideToUser()}方法中进行相应的操作}
 */
public abstract class BaseDialogFragment extends DialogFragment implements IView {

    public String TAG = this.getClass().getSimpleName();

    protected Window mWindow;
    protected Activity activity;
    protected View contentView;
    private Unbinder unbinder;
    private boolean visibleToUser;
    protected LoadingDialog loadingDialog;
    public int mWidth;
    public int mHeight;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        //点击外部消失
        dialog.setCanceledOnTouchOutside(true);
        mWindow = dialog.getWindow();
        ScreenUtils.getScreenDensity();
        mWidth = ScreenUtils.getScreenWidth();
        mHeight = ScreenUtils.getScreenHeight();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        if (contentView == null) {
            contentView = inflater.inflate(getLayoutId(), container, false);
        }
        ViewGroup parent = (ViewGroup) contentView.getParent();
        if (parent != null) {
            parent.removeView(contentView);
        }
        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //绑定到butterknife
        unbinder = ButterKnife.bind(this, contentView);
        initViews(savedInstanceState);
        initEvents();
        if (enableEventBus() && !EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != Unbinder.EMPTY) unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        this.contentView = null;
        this.unbinder = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mWidth = ScreenUtils.getScreenWidth();
        mHeight = ScreenUtils.getScreenHeight();
    }

    /**
     * 情况一、当Fragment嵌套在Fragment中时，调用父Fragment的hide方法进行切换会调用此方法
     *
     * @param hidden true表示不可见
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            // 相当于Fragment的onResume
            onVisibleToUser();
        } else {
            onHideToUser();
            // 相当于Fragment的onPause
        }
        needChangeChild(!hidden);
    }

    /**
     * 情况二、当父容器是ViewPager时，切换ViewPager会调用此方法
     *
     * @param isVisibleToUser true表示可见
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isAdded()) {
            if (isVisibleToUser) {
                // 相当于onResume()方法
                onVisibleToUser();
            } else {
                // 相当于onpause()方法
                onHideToUser();
            }
            needChangeChild(isVisibleToUser);
        }
    }

    /**
     * 情况三、当Fragment回到屏幕视图顶端时，判断Fragment的可见性
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "getUserVisibleHint() : " + getUserVisibleHint());
        Log.i(TAG, "isVisible() : " + isVisible());
        Log.i(TAG, "isParentVisible() : " + isParentVisible());
        if (getUserVisibleHint() && isVisible() && isParentVisible()) {
            // 相当于Fragment的onResume
            onVisibleToUser();
        } else {
            onHideToUser();
            // 相当于Fragment的onPause
        }
    }

    /**
     * 如果是嵌套在Fragment中，判断父Fragment是否可见
     * 注意：父Fragment需要使用getChildFragmentManager方法进行fragment的add操作，否则getParentFragment()将获取到null
     */
    private boolean isParentVisible() {
        //这里获取到父Fragment，父Fragment需要使用getChildFragmentManager方法进行fragment的add操作
        Fragment parent = getParentFragment();
        boolean parentVisible = true;
        if (parent != null) {
            parentVisible = parent.getUserVisibleHint() && parent.isVisible();
        }
        return parentVisible;
    }

    /**
     * 判断Fragment是否嵌套了子Fragment,如果嵌套了，则通知子Fragment是否可见
     *
     * @param visible true表示可见
     */
    private void needChangeChild(boolean visible) {
        if (isAdded() && getChildFragmentManager() != null) {
            List<Fragment> childFragments = getChildFragmentManager().getFragments();
            if (childFragments != null && childFragments.size() > 0) {
                for (Fragment childFragment : childFragments) {
                    if (childFragment instanceof BaseDialogFragment) {
                        BaseDialogFragment baseChildFragment = (BaseDialogFragment) childFragment;
                        if (visible && childFragment.getUserVisibleHint()
                                && childFragment.isVisible()) {
                            baseChildFragment.onVisibleToUser();
                        } else {
                            baseChildFragment.onHideToUser();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onHideToUser();
    }

    public boolean isVisibleToUser() {
        return visibleToUser;
    }

    protected void onVisibleToUser() {
        //Log.i(TAG, this + "onVisibleToUser is called", new Throwable());
        visibleToUser = true;
    }

    protected void onHideToUser() {
        //Log.i(TAG, this + "onHideToUser is called", new Throwable());
        visibleToUser = false;
    }

    @Override
    public void showLoading() {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getContext());
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

    protected abstract int getLayoutId();

    protected abstract void initViews(Bundle savedInstanceState);

    protected abstract void initEvents();

    /**
     * 注册event bus
     */
    protected boolean enableEventBus() {
        return true;
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
        // handle event
    }

    /**
     * 是否可以使用沉浸式
     */
    protected boolean enableImmersionBar() {
        return true;
    }

    @Override
    public void toast(CharSequence s) {
        ToastUtils.showShortToast(s);
    }

    protected boolean isCheckAppUpdate() {
        return true;
    }
}
