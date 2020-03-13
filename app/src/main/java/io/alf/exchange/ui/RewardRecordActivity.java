package io.alf.exchange.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.RewardRecordBean;
import io.alf.exchange.mvp.presenter.RewardRecordPresenter;
import io.alf.exchange.mvp.view.RewardRecordView;
import io.alf.exchange.ui.adapter.RewardRecordAdapter;
import io.alf.exchange.util.EmptyViewUtil;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.DateUtil;

public class RewardRecordActivity extends MvpActivity implements RewardRecordView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.recyclerView1)
    RecyclerView recyclerView1;

    private List<RewardRecordBean> mDepositRewardData = new ArrayList<>();
    private RewardRecordAdapter mDepositRewardRecordAdapter;
    private RewardRecordPresenter mRewardRecordPresenter;
    private boolean loadMoreEnd;
    private long pageIndex = 1;

    @Override
    protected void initPresenter() {
        mRewardRecordPresenter = registerPresenter(new RewardRecordPresenter(), this);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reward_record;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("奖励发放记录");
        initDepositRewardAdapter();
    }

    private void initDepositRewardAdapter() {
        mDepositRewardRecordAdapter = new RewardRecordAdapter();
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerView1.setAdapter(mDepositRewardRecordAdapter);
        mDepositRewardRecordAdapter.setOnLoadMoreListener(
                new BaseQuickAdapter.RequestLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        String address = WalletUtils.getAddress();
                        if (!StringUtils.isEmpty(address)) {
                            if (loadMoreEnd) {
                                mDepositRewardRecordAdapter.loadMoreEnd();
                            } else {
                                pageIndex++;
                                long endDate = System.currentTimeMillis();
                                long beginDate = endDate - pageIndex * 140 * 24 * 60 * 60 * 1000;
                                String queryEndDate = DateUtil.formatDateTime(
                                        DateUtil.FMT_STD_DATE_TIME, endDate);
                                String queryBeginDate = DateUtil.formatDateTime(
                                        DateUtil.FMT_STD_DATE_TIME, beginDate);

                                mRewardRecordPresenter.queryRewardRecord(address, queryBeginDate,
                                        queryEndDate);
                            }
                        }

                    }
                }, recyclerView1);
        mDepositRewardRecordAdapter.setEnableLoadMore(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            loadMoreEnd = false;
            long endDate = System.currentTimeMillis();
            long beginDate = endDate - pageIndex * 140 * 24 * 60 * 60 * 1000;
            String queryEndDate = DateUtil.formatDateTime(DateUtil.FMT_STD_DATE_TIME, endDate);
            String queryBeginDate = DateUtil.formatDateTime(DateUtil.FMT_STD_DATE_TIME, beginDate);
            mRewardRecordPresenter.queryRewardRecord(address, queryBeginDate, queryEndDate);
            mRewardRecordPresenter.queryRewardRecord(address, queryBeginDate, queryEndDate);
        }
    }

    @Override
    protected void initEvents() {

    }

    @Override
    public void onQueryRewardRecord(boolean success, List<RewardRecordBean> dataList) {
        if (success) {
            mDepositRewardRecordAdapter.loadMoreComplete();
            if (dataList != null && dataList.size() > 0) {
                if (mDepositRewardData.size() == dataList.size()) {
                    loadMoreEnd = true;
                }
                mDepositRewardData.clear();
                mDepositRewardData.addAll(dataList);
                mDepositRewardRecordAdapter.setNewData(mDepositRewardData);
                mDepositRewardRecordAdapter.notifyDataSetChanged();
            } else {
                mDepositRewardRecordAdapter.setNewData(null);
                View emptyView = EmptyViewUtil.setEmpty(recyclerView1, this, "暂无记录", 30);
                mDepositRewardRecordAdapter.setEmptyView(emptyView);
                mDepositRewardRecordAdapter.notifyDataSetChanged();
            }
        } else {
            mDepositRewardRecordAdapter.loadMoreFail();
        }
    }
}
