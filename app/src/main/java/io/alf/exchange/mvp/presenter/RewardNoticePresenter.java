package io.alf.exchange.mvp.presenter;

import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

import io.alf.exchange.mvp.bean.RewardNoticeBean;
import io.alf.exchange.mvp.view.RewardNoticeView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class RewardNoticePresenter extends BasePresenter<RewardNoticeView> {
    public void queryRewardNotice(String platformAddress) {
        Map<String, Object> data = new HashMap<>();
        data.put("platformAddress", platformAddress);
        HttpUtils.postRequest(BaseUrl.QUERY_REWARD_NOTICE, getView(), generateRequestHeader(),
                generateRequestBody(data), new BaseCallback<ResponseBean<RewardNoticeBean>>(
                        BaseUrl.QUERY_REWARD_NOTICE, getView()) {
                    @Override
                    public void onSuccess(
                            Response<ResponseBean<RewardNoticeBean>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body() != null) {
                            getView().onQueryRewardNotice(response.body().isSuccess(),
                                    response.body().data);
                        }
                    }
                });
    }
}
