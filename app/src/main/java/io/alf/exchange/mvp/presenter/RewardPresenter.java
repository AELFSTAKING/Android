package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.alf.exchange.mvp.bean.DepositRewardBean;
import io.alf.exchange.mvp.bean.MiningRewardBean;
import io.alf.exchange.mvp.view.RewardView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class RewardPresenter extends BasePresenter<RewardView> {

    public void queryDepositReward(String platformAddress) {
        Map<String, Object> data = new HashMap<>();
        data.put("platformAddress", platformAddress);
        HttpUtils.postRequest(BaseUrl.QUERY_DEPOSIT_REWARD, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<List<DepositRewardBean>>>(
                        BaseUrl.QUERY_DEPOSIT_REWARD, getView()) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<List<DepositRewardBean>>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onQueryDepositReward(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }

    public void queryMiningReward(String platformAddress) {
        Map<String, Object> data = new HashMap<>();
        data.put("platformAddress", platformAddress);
        HttpUtils.postRequest(BaseUrl.QUERY_MINING_REWARD, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<List<MiningRewardBean>>>(
                        BaseUrl.QUERY_MINING_REWARD, getView()) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<List<MiningRewardBean>>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onQueryMiningReward(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }
}
