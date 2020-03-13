package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.RewardNoticeBean;
import io.tick.base.mvp.IView;

public interface RewardNoticeView extends IView {
    void onQueryRewardNotice(boolean success, RewardNoticeBean data);
}
