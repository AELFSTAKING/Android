package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.alf.exchange.mvp.bean.RewardRecordBean;
import io.alf.exchange.mvp.view.RewardRecordView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class RewardRecordPresenter extends BasePresenter<RewardRecordView> {

    public final static String DEPOSIT_TYPE = "1";
    public final static String MINING_TYPE = "2";

    public void queryRewardRecord(String platformAddress, String queryBeginDate,
            String queryEndDate) {
        Map<String, Object> data = new HashMap<>();
        data.put("platformAddress", platformAddress);
        data.put("queryBeginDate", queryBeginDate);
        data.put("queryEndDate", queryEndDate);
        HttpUtils.postRequest(BaseUrl.QUERY_REWARD_RECORD, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<List<RewardRecordBean>>>(
                        BaseUrl.QUERY_REWARD_RECORD, getView()) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<List<RewardRecordBean>>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onQueryRewardRecord(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }
}
