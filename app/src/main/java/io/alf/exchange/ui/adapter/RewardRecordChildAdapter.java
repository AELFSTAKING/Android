package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.RewardRecordBean;

public class RewardRecordChildAdapter extends
        BaseQuickAdapter<RewardRecordBean.SendListBean, BaseViewHolder> {

    public RewardRecordChildAdapter() {
        super(R.layout.item_reward_record_child);
    }

    @Override
    protected void convert(BaseViewHolder helper, RewardRecordBean.SendListBean item) {
        helper.setText(R.id.tv_currency, item.tokenCurrency);
        helper.setText(R.id.tv_amount, item.rewardAmount + " " + item.rewardCurrency);
    }
}
