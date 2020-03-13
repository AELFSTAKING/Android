package io.alf.exchange.mvp.view;


import java.util.List;

import io.alf.exchange.mvp.bean.RewardRecordBean;
import io.tick.base.mvp.IView;

public interface RewardRecordView extends IView {
    void onQueryRewardRecord(boolean success, List<RewardRecordBean> dataList);
}
