package io.alf.exchange.ui;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vector.update_app.utils.AppUpdateUtils;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.presenter.QueryLatestVersionPresenter;
import io.alf.exchange.mvp.view.QueryLatestVersionView;
import io.tick.base.appupdate.AppUpdateBean;
import io.tick.base.mvp.MvpFragment;
import io.tick.base.net.BaseUrl;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.RxBindingUtils;

public class MineFragment extends MvpFragment implements QueryLatestVersionView {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_settings)
    RelativeLayout rlSettings;
    @BindView(R.id.rl_version)
    RelativeLayout rlVersion;
    @BindView(R.id.tv_version)
    TextView tvVersion;

    private QueryLatestVersionPresenter mQueryLatestVersionPresenter;

    @Override
    protected void initPresenter() {
        mQueryLatestVersionPresenter = registerPresenter(new QueryLatestVersionPresenter(), this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        tvTitle.setText("我的");
        tvVersion.setText("v " + AppUpdateUtils.getVersionName(getContext()));
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> {
            ActivityStartUtils.jump(getContext(), SettingsActivity.class);
        }, rlSettings);
        RxBindingUtils.clicks(v -> {
            mQueryLatestVersionPresenter.queryLatestVersion(
                    AppUpdateUtils.getVersionName(getContext()));
        }, rlVersion);
    }

    @Override
    public void onQueryLatestVersion(AppUpdateBean data) {
        if (data != null) {
            if (data.isForcedUpdate() || data.isUpdate()) {
                toast("正在获取新版本");
                io.tick.base.appupdate.AppUpdateUtils.checkUpdate(getActivity(),
                        BaseUrl.UPDATE_URL);
            } else {
                toast("已是最新版本");
            }
        }
    }
}
