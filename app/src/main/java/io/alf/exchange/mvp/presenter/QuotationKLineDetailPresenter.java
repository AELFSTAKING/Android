package io.alf.exchange.mvp.presenter;

import android.text.TextUtils;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.Constant;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.QuotationHistoryBean;
import io.alf.exchange.mvp.view.QuotationKLineDetailView;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;


public class QuotationKLineDetailPresenter extends BasePresenter<QuotationKLineDetailView> {

    public void queryQuotationHistory(String symbol, long startTime, long endTime, String range) {
        Map<String, Object> data = new HashMap<>();
        data.put("symbol", symbol);
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("range", range);
        getView().onChangeRefreshStatus(true);
        HttpUtils.postRequest(BaseUrl.QUERY_QUOTATION_HISTORY, getView(), generateRequestHeader(),
                data, new BaseCallback<ResponseBean<QuotationHistoryBean>>(
                        BaseUrl.QUERY_QUOTATION_HISTORY) {
                    @Override
                    public void onSuccess(Response<ResponseBean<QuotationHistoryBean>> response) {
                        super.onSuccess(response);
                        getView().onChangeRefreshStatus(false);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null && response.body().isSuccess()) {
                            getView().onQueryQuotationHistory(response.body().data);
                        }
                    }

                    @Override
                    public void onError(Response<ResponseBean<QuotationHistoryBean>> response) {
                        super.onError(response);
                        getView().onChangeRefreshStatus(false);
                        getView().onQueryQuotationHistoryError();
                    }
                });
    }

    public void queryQuotationHistory(String symbol, String range) {
        long endTime = System.currentTimeMillis();
        long startTime = getStartTime(endTime, range);
        queryQuotationHistory(symbol, startTime, endTime, range);
    }

    private long getStartTime(long endTime, String range) {
        //return endTime - 3600 * Long.valueOf(range);
        return endTime - 360 * Long.valueOf(range);
    }

    public void saveSelectedKLineItem(String selectedItem, boolean isMinuteHour) {
        CexDataPersistenceUtils.putLatestKLineRange(selectedItem);
        CexDataPersistenceUtils.setMinuteHourKLine(isMinuteHour);
    }

    public String getSelectedKLineItem() {
        return CexDataPersistenceUtils.getLatestKLineRange();
    }

    public boolean isMinuteHour() {
        return CexDataPersistenceUtils.isMinuteHourKLine();
    }

    public int getLatestKLineItemTitleRes() {
        if (CexDataPersistenceUtils.isMinuteHourKLine()) {
            return R.string.k_time;
        }
        String item = CexDataPersistenceUtils.getLatestKLineRange();
        if (TextUtils.equals(item, Constant.MIN_1)) {
            return R.string.k_1min;
        } else if (TextUtils.equals(item, Constant.MIN_5)) {
            return R.string.k_5min;
        } else if (TextUtils.equals(item, Constant.MIN_15)) {
            return R.string.k_15min;
        } else if (TextUtils.equals(item, Constant.MIN_30)) {
            return R.string.k_30min;
        } else if (TextUtils.equals(item, Constant.HOUR_1)) {
            return R.string.k_1h;
        } else if (TextUtils.equals(item, Constant.HOUR_2)) {
            return R.string.k_2h;
        } else if (TextUtils.equals(item, Constant.HOUR_4)) {
            return R.string.k_4h;
        } else if (TextUtils.equals(item, Constant.HOUR_6)) {
            return R.string.k_6h;
        } else if (TextUtils.equals(item, Constant.HOUR_12)) {
            return R.string.k_12h;
        } else if (TextUtils.equals(item, Constant.DAY_1)) {
            return R.string.k_1d;
        } else if (TextUtils.equals(item, Constant.WEEK_1)) {
            return R.string.k_1w;
        }
        return 0;
    }
}
