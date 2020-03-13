package io.alf.exchange.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.dialog.PasswordDialog;
import io.alf.exchange.mvp.bean.CancelOrderBean;
import io.alf.exchange.mvp.bean.OrderHistoryBean;
import io.alf.exchange.mvp.bean.SendTxBean;
import io.alf.exchange.mvp.presenter.CancelOrderPresenter;
import io.alf.exchange.mvp.presenter.DelegatePresenter;
import io.alf.exchange.mvp.view.CancelOrderView;
import io.alf.exchange.mvp.view.DelegateView;
import io.alf.exchange.ui.adapter.DelegateAdapter2;
import io.alf.exchange.ui.adapter.HistoryDelegateAdapter2;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.EmptyViewUtil;
import io.alf.exchange.util.SignTxUtil;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.Validator;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.eventbus.EventBusCenter;
import io.tick.base.eventbus.EventCode;
import io.tick.base.mvp.MvpFragment;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.RxBindingUtils;

public class DelegateFragment extends MvpFragment implements DelegateView, CancelOrderView {

    @BindView(R.id.tl_tab)
    TabLayout tabLayout;
    @BindView(R.id.ll_tab_content1)
    LinearLayout llTabContent1;
    @BindView(R.id.recycler_view_1)
    RecyclerView recyclerView1;
    @BindView(R.id.ll_tab_content2)
    LinearLayout llTabContent2;
    @BindView(R.id.recycler_view_2)
    RecyclerView recyclerView2;
    @BindView(R.id.tv_all)
    TextView tvAll;

    private PasswordDialog mDialog;

    private List<String> tabs = new ArrayList<>();

    private DelegatePresenter mDelegatePresenter;
    private DelegateAdapter2 mCurrentDelegateAdapter;
    private HistoryDelegateAdapter2 mHistoryDelegateAdapter;
    private CancelOrderPresenter mCancelOrderPresenter;

    private List<OrderHistoryBean.ListBean> currentDelegateOrderList = new ArrayList<>();
    private List<OrderHistoryBean.ListBean> historyDelegateOrderList = new ArrayList<>();
    private int currentDelegatePageIndex = 1;
    private int historyDelegatePageIndex = 1;
    private int pageSize = 10;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_delegate;
    }

    @Override
    protected void initPresenter() {
        mDelegatePresenter = registerPresenter(new DelegatePresenter(), this);
        mCancelOrderPresenter = registerPresenter(new CancelOrderPresenter(), this);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initTabView();
        initAdapter();
    }

    private void initAdapter() {
        mCurrentDelegateAdapter = new DelegateAdapter2();
        recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView1.setAdapter(mCurrentDelegateAdapter);
        mCurrentDelegateAdapter.setOnLoadMoreListener(
                new BaseQuickAdapter.RequestLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        String address = WalletUtils.getAddress();
                        if (!StringUtils.isEmpty(address)) {
                            if (currentDelegateOrderList.size()
                                    == currentDelegatePageIndex * pageSize) {
                                //加载更多数据逻辑
                                currentDelegatePageIndex++;
                                mDelegatePresenter.queryCurrentOrderList(address,
                                        currentDelegatePageIndex * pageSize);
                            } else {
                                mCurrentDelegateAdapter.loadMoreEnd();
                            }
                        }
                    }
                }, recyclerView1);
        mCurrentDelegateAdapter.setEnableLoadMore(true);

        mHistoryDelegateAdapter = new HistoryDelegateAdapter2();
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView2.setAdapter(mHistoryDelegateAdapter);
        mHistoryDelegateAdapter.setOnLoadMoreListener(
                new BaseQuickAdapter.RequestLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        String address = WalletUtils.getAddress();
                        if (!StringUtils.isEmpty(address)) {
                            if (historyDelegateOrderList.size()
                                    == historyDelegatePageIndex * pageSize) {
                                //加载更多数据逻辑
                                historyDelegatePageIndex++;
                                mDelegatePresenter.queryHistoryOrderList(WalletUtils.getAddress(),
                                        historyDelegatePageIndex * pageSize);
                            } else {
                                mHistoryDelegateAdapter.loadMoreEnd();
                            }
                        }
                    }
                }, recyclerView2);
        mHistoryDelegateAdapter.setEnableLoadMore(true);
    }

    @Override
    protected void initEvents() {
        mCurrentDelegateAdapter.setOnItemChildClickListener(
                new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view,
                            int position) {
                        OrderHistoryBean.ListBean bean = currentDelegateOrderList.get(position);
                        mDialog = new PasswordDialog(getContext(), v -> {
                            TextView passwordView = (TextView) v;
                            if (Validator.checkPassword(getContext(), passwordView)) {
                                mCancelOrderPresenter.cancelUserOrder(bean.orderNo,
                                        passwordView.getText().toString());
                                mDialog.dismiss();
                            }
                        });
                        mDialog.show();

                    }
                });
        mCurrentDelegateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OrderHistoryBean.ListBean bean = currentDelegateOrderList.get(position);
                OrderDetailActivity.startUp(getContext(), bean);
            }
        });

        mHistoryDelegateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                OrderHistoryBean.ListBean bean = historyDelegateOrderList.get(position);
                OrderDetailActivity.startUp(getContext(), bean);
            }
        });

        RxBindingUtils.clicks(v -> {
            ActivityStartUtils.jump(getContext(), DelegateActivity.class);
        }, tvAll);
    }

    private void initTabView() {
        tabs.add("当前委托");
        tabs.add("历史委托");
        TextView tabTitle;
        for (int i = 0; i < tabs.size(); i++) {
            //获取tab
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            //给tab设置自定义布局
            Objects.requireNonNull(tab).setCustomView(R.layout.item_tab_home);
            tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(R.id.tv_title);
            //填充数据
            tabTitle.setText(tabs.get(i));
            //默认选择第一项
            if (i == 0) {
                tabTitle.setSelected(true);
                tabTitle.setTextSize(16);
                tabTitle.setTextColor(Color.parseColor("#FF291F59"));
                changeTab();
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(
                        R.id.tv_title);
                tabTitle.setSelected(true);
                tabTitle.setTextSize(16);
                tabTitle.setTextColor(Color.parseColor("#FF291F59"));
                changeTab();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tabTitle = Objects.requireNonNull(tab.getCustomView()).findViewById(
                        R.id.tv_title);
                tabTitle.setSelected(true);
                tabTitle.setTextSize(13);
                tabTitle.setTextColor(Color.parseColor("#FF9B9EBB"));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void changeTab() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            llTabContent2.setVisibility(View.GONE);
            llTabContent1.setVisibility(View.VISIBLE);
        } else if (tabLayout.getSelectedTabPosition() == 1) {
            llTabContent1.setVisibility(View.GONE);
            llTabContent2.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CexDataPersistenceUtils.isLogin()) {
            String address = WalletUtils.getAddress();
            if (!StringUtils.isEmpty(address)) {
                mDelegatePresenter.queryCurrentOrderList(address,
                        currentDelegatePageIndex * pageSize);
                mDelegatePresenter.queryHistoryOrderList(address,
                        currentDelegatePageIndex * pageSize);
            }
        }
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
        if (CexDataPersistenceUtils.isLogin()) {
            String address = WalletUtils.getAddress();
            if (!StringUtils.isEmpty(address)) {
                mDelegatePresenter.queryCurrentOrderList(address,
                        currentDelegatePageIndex * pageSize);
                mDelegatePresenter.queryHistoryOrderList(address,
                        currentDelegatePageIndex * pageSize);
            }
        }
    }

    @Override
    public void onQueryCurrentOrderList(boolean success, OrderHistoryBean data) {
        if (success) {
            mCurrentDelegateAdapter.loadMoreComplete();
            if (data != null && data.list != null && data.list.size() > 0) {
                currentDelegateOrderList.clear();
                currentDelegateOrderList.addAll(data.list);
                mCurrentDelegateAdapter.setNewData(currentDelegateOrderList);
                mCurrentDelegateAdapter.notifyDataSetChanged();
            } else {
                mCurrentDelegateAdapter.setNewData(null);
                View emptyView = EmptyViewUtil.setEmpty(recyclerView1, getContext(), "暂无订单信息", 20);
                mCurrentDelegateAdapter.setEmptyView(emptyView);
                mCurrentDelegateAdapter.notifyDataSetChanged();
            }
        } else {
            mCurrentDelegateAdapter.loadMoreFail();
        }
    }

    @Override
    public void onQueryHistoryOrderList(boolean success, OrderHistoryBean data) {
        if (success) {
            mHistoryDelegateAdapter.loadMoreComplete();
            if (data != null && data.list != null && data.list.size() > 0) {
                historyDelegateOrderList.clear();
                historyDelegateOrderList.addAll(data.list);
                mHistoryDelegateAdapter.setNewData(historyDelegateOrderList);
                mHistoryDelegateAdapter.notifyDataSetChanged();
            } else {
                mHistoryDelegateAdapter.setNewData(null);
                View emptyView = EmptyViewUtil.setEmpty(recyclerView1, getContext(), "暂无订单信息", 20);
                mHistoryDelegateAdapter.setEmptyView(emptyView);
                mHistoryDelegateAdapter.notifyDataSetChanged();
            }
        } else {
            mHistoryDelegateAdapter.loadMoreFail();
        }
    }

    @Override
    public void onCancelUserOrder(boolean success, CancelOrderBean data, String password) {
        if (data != null) {
            String signedRawTransaction = SignTxUtil.signTx(
                    data.createStackingTxResp.rawTransaction, password);
            mCancelOrderPresenter.sendCancelOrderTx(data.orderCancelMessage.orderNo,
                    signedRawTransaction);
        }
    }

    @Override
    protected void onEventCallback(EventBusCenter event) {
        super.onEventCallback(event);
        if (isVisibleToUser()) {
            if (event.code == EventCode.UPDATE_DELEGATE) {
                if (CexDataPersistenceUtils.isLogin()) {
                    String address = WalletUtils.getAddress();
                    if (!StringUtils.isEmpty(address)) {
                        mDelegatePresenter.queryCurrentOrderList(address,
                                currentDelegatePageIndex * pageSize);
                        mDelegatePresenter.queryHistoryOrderList(address,
                                historyDelegatePageIndex * pageSize);
                    }
                }
            }
            long currentTime = System.currentTimeMillis();
            // 10s 刷新一次
            if ((currentTime - lastUpdateTime) > 10000) {
                lastUpdateTime = currentTime;
                if (CexDataPersistenceUtils.isLogin()) {
                    String address = WalletUtils.getAddress();
                    if (!StringUtils.isEmpty(address)) {
                        mDelegatePresenter.queryCurrentOrderList(address,
                                currentDelegatePageIndex * pageSize);
                        mDelegatePresenter.queryHistoryOrderList(address,
                                historyDelegatePageIndex * pageSize);
                    }
                }
            }
        }
    }

    private static long lastUpdateTime = 0;

    @Override
    public void onSendCancelOrderTx(String orderId, SendTxBean data) {
        toast("撤单请求已发送");
        if (data != null) {
            mCancelOrderPresenter.cancelCallback(orderId, data.txHash);
        }
        //deleteCancelOrderItem(orderId);
    }

    @Override
    public void onCancelCallback(String orderId, boolean success) {

    }

    private void deleteCancelOrderItem(String orderNo) {
        if (currentDelegateOrderList != null) {
            Iterator<OrderHistoryBean.ListBean> it = currentDelegateOrderList.iterator();
            while (it.hasNext()) {
                OrderHistoryBean.ListBean bean = it.next();
                if (null != bean && StringUtils.equals(bean.orderNo, orderNo)) {
                    it.remove();
                }
            }
        }
        mCurrentDelegateAdapter.setNewData(currentDelegateOrderList);
        mCurrentDelegateAdapter.notifyDataSetChanged();
    }
}
