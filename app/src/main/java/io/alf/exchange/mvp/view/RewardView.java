package io.alf.exchange.mvp.view;


import java.util.List;

import io.alf.exchange.mvp.bean.DepositRewardBean;
import io.alf.exchange.mvp.bean.MiningRewardBean;
import io.tick.base.mvp.IView;

public interface RewardView extends IView {
    void onQueryDepositReward(boolean success, List<DepositRewardBean> data);

    void onQueryMiningReward(boolean success, List<MiningRewardBean> data);
}
