package io.alf.exchange.mvp.presenter;


import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.view.QueryLatestVersionView;
import io.tick.base.appupdate.AppUpdateBean;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class QueryLatestVersionPresenter extends BasePresenter<QueryLatestVersionView> {

    public void queryLatestVersion(String versionName) {
        Map<String, Object> data = new HashMap<>();
        data.put("os", 2);
        data.put("version", versionName);
        HttpUtils.postRequest(BaseUrl.UPDATE_URL, getView(), generateRequestHeader(), generateRequestBody(data),
                new BaseCallback<ResponseBean<AppUpdateBean>>(BaseUrl.UPDATE_URL, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<AppUpdateBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                            if (response.body().isSuccess()) {
                                getView().onQueryLatestVersion(response.body().data);
                            }
                        }
                    }
                });
    }
}
