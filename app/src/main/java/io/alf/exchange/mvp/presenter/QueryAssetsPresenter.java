package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.TotalAssetBean;
import io.alf.exchange.mvp.view.QueryAssetsView;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class QueryAssetsPresenter extends BasePresenter<QueryAssetsView> {

    public void queryAssetList(String address) {
        Map<String, Object> data = new HashMap<>();
        data.put("address", address);
        HttpUtils.postRequest(BaseUrl.QUERY_TOTAL_ASSET, getView(), generateRequestHeader(), data,
                new BaseCallback<ResponseBean<TotalAssetBean>>(BaseUrl.QUERY_TOTAL_ASSET) {
                    @Override
                    public void onSuccess(Response<ResponseBean<TotalAssetBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            CexDataPersistenceUtils.putTotalAssetBean(response.body().data);
                            getView().onQueryAssetList(response.body().data);
                        }
                    }
                });
    }
}
