package io.alf.exchange.mvp.presenter;


import com.lzy.okgo.model.Response;

import java.util.List;

import io.alf.exchange.mvp.view.QueryGroupQuotationByAreaView;
import io.alf.exchange.util.FilterHideQuotationUtil;
import io.cex.mqtt.bean.QuotationGroupByAreaBean;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryGroupQuotationByAreaPresenter extends
        BasePresenter<QueryGroupQuotationByAreaView> {

    public void queryQuotationsGroupBy(int actionId, boolean showLoading) {
        HttpUtils.postRequest(BaseUrl.QUERY_QUOTATION_GROUP_BY_AREA, getView(),
                generateRequestHeader(), generateRequestBody(),
                new BaseCallback<ResponseBean<List<QuotationGroupByAreaBean>>>(
                        BaseUrl.QUERY_QUOTATION_GROUP_BY_AREA, getView(), showLoading) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<List<QuotationGroupByAreaBean>>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryQuotationsGroupByArea(actionId,
                                    FilterHideQuotationUtil.filterHideQuotationsGroupByArea(
                                            response.body().data));
                        }
                    }
                });
    }

    public void queryQuotationsGroupBy(int actionId) {
        queryQuotationsGroupBy(actionId, false);
    }
}
