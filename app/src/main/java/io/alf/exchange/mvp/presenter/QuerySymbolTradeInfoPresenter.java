package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.TradeSymbolInfoBean;
import io.alf.exchange.mvp.view.QuerySymbolTradeInfoView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;
import io.tick.base.util.BigDecimalUtil;


public class QuerySymbolTradeInfoPresenter extends BasePresenter<QuerySymbolTradeInfoView> {

    public void queryTradeSymbolInfo(String symbol, String address) {
        queryTradeSymbolInfo(symbol, address, false);
    }

    /**
     * 委托发起--查询
     */
    public void queryTradeSymbolInfo(String symbol, String address, boolean showLoading) {
        Map<String, Object> data = new HashMap<>();
        data.put("symbol", symbol);
        data.put("address", address);
        HttpUtils.postRequest(BaseUrl.QUERY_TRADE_SYMBOL_INFO, getView(), generateRequestHeader(),
                data,
                new BaseCallback<ResponseBean<TradeSymbolInfoBean>>(BaseUrl.QUERY_TRADE_SYMBOL_INFO,
                        getView(), showLoading) {
                    @Override
                    public void onSuccess(Response<ResponseBean<TradeSymbolInfoBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            TradeSymbolInfoBean data = response.body().data;
                            if (data != null) {
                                data.availableBuy = BigDecimalUtil.stripTrailingZeros(
                                        BigDecimalUtil.formatByDecimalValue(data.availableBuy));
                                data.availableSell = BigDecimalUtil.stripTrailingZeros(
                                        BigDecimalUtil.formatByDecimalValue(data.availableSell));
                            }
                            getView().onQueryTradeSymbolInfo(response.body().isSuccess(), data);
                        }
                    }
                });
    }
}
