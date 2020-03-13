package io.alf.exchange.mvp.presenter;


import com.lzy.okgo.model.Response;

import java.util.List;

import io.alf.exchange.mvp.view.QuotationsView;
import io.alf.exchange.util.FilterHideQuotationUtil;
import io.cex.mqtt.bean.QuotationBean;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QuotationsPresenter extends BasePresenter<QuotationsView> {

    public void querySymbolQuotation() {
        HttpUtils.postRequest(BaseUrl.QUERY_SYMBOL_QUOTATION, getView(), generateRequestHeader(),
                generateRequestBody(), new BaseCallback<ResponseBean<List<QuotationBean>>>(
                        BaseUrl.QUERY_SYMBOL_QUOTATION, getView()) {
                    @Override
                    public void onSuccess(Response<ResponseBean<List<QuotationBean>>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQuerySymbolQuotation(
                                    FilterHideQuotationUtil.filterHideQuotation(
                                            response.body().data));
                        }
                    }
                });
    }
}
