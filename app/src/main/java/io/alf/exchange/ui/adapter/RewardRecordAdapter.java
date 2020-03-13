package io.alf.exchange.ui.adapter;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.RewardRecordBean;
import io.alf.exchange.util.EmptyViewUtil;

public class RewardRecordAdapter extends BaseQuickAdapter<RewardRecordBean, BaseViewHolder> {

    public RewardRecordAdapter() {
        super(R.layout.item_reward_record);
    }

    @Override
    protected void convert(BaseViewHolder helper, RewardRecordBean item) {
        switch (item.rewardType) {
            case "1": {
                helper.setTextColor(R.id.tv_deposit_title,
                        mContext.getResources().getColor(R.color.color_FF641DAF));
                helper.setText(R.id.tv_deposit_title, "充值奖励");
                break;
            }
            case "2": {
                helper.setTextColor(R.id.tv_deposit_title,
                        mContext.getResources().getColor(R.color.color_FFF79B2C));
                helper.setText(R.id.tv_deposit_title, "挖矿奖励");
                break;
            }
        }
        helper.setText(R.id.tv_deposit_time, item.sendTime);
        RewardRecordChildAdapter adapter = new RewardRecordChildAdapter();
        RecyclerView recyclerView = helper.getView(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter);
        if (item.sendList != null && item.sendList.size() > 0) {
            adapter.setNewData(item.sendList);
            adapter.notifyDataSetChanged();
        } else {
            adapter.setNewData(null);
            View emptyView = EmptyViewUtil.setEmpty(recyclerView, mContext, "暂无记录", 10);
            adapter.setEmptyView(emptyView);
            adapter.notifyDataSetChanged();
        }
    }
}
