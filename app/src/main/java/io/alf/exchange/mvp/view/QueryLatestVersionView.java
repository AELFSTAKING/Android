package io.alf.exchange.mvp.view;

import io.tick.base.appupdate.AppUpdateBean;
import io.tick.base.mvp.IView;

public interface QueryLatestVersionView extends IView {
    void onQueryLatestVersion(AppUpdateBean data);
}
