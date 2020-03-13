package io.alf.exchange.mvp.presenter;


import com.lzy.okgo.model.Response;

import java.util.List;

import io.alf.exchange.mvp.view.QueryGroupQuotationView;
import io.alf.exchange.util.FilterHideQuotationUtil;
import io.cex.mqtt.bean.QuotationGroupBean;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryGroupQuotationPresenter extends BasePresenter<QueryGroupQuotationView> {

    public void queryQuotationsGroupBy(int actionId, boolean showLoading) {
        HttpUtils.postRequest(BaseUrl.QUERY_QUOTATION_GROUP_BY, getView(), generateRequestHeader(),
                generateRequestBody(),
                new BaseCallback<ResponseBean<List<QuotationGroupBean>>>(
                        BaseUrl.QUERY_QUOTATION_GROUP_BY, getView(), showLoading) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<List<QuotationGroupBean>>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryQuotationsGroupBy(actionId,
                                    FilterHideQuotationUtil.filterHideQuotationsGroupBy(
                                            response.body().data));
                        }
                    }
                });
    }

    public void queryQuotationsGroupBy(int actionId) {
        queryQuotationsGroupBy(actionId, false);
    }
}
