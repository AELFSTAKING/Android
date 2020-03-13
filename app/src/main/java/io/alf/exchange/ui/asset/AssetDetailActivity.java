package io.alf.exchange.ui.asset;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.dialog.BindAddressDialog;
import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.mvp.bean.TxHistoryBean;
import io.alf.exchange.mvp.presenter.QueryBindAddressPresenter;
import io.alf.exchange.mvp.presenter.QueryTxHistoryPresenter;
import io.alf.exchange.mvp.view.QueryBindAddressView;
import io.alf.exchange.mvp.view.QueryTxHistoryView;
import io.alf.exchange.ui.TxRecordDetailActivity;
import io.alf.exchange.ui.adapter.TxRecordAdapter;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.EmptyViewUtil;
import io.alf.exchange.util.PriceConvertUtil;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.CopyUtil;
import io.tick.base.util.RxBindingUtils;

public class AssetDetailActivity extends MvpActivity implements QueryBindAddressView,
        QueryTxHistoryView {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_legal_balance)
    TextView tvLegalBalance;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_deposit)
    TextView tvDeposit;
    @BindView(R.id.tv_receive)
    TextView tvReceive;
    @BindView(R.id.tv_withdraw)
    TextView tvWithdraw;
    @BindView(R.id.tv_transfer)
    TextView tvTransfer;
    @BindView(R.id.tv_copy)
    TextView tvCopyAddress;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private List<TxHistoryBean.TransactionsBean> txRecordList = new ArrayList<>();
    private TxRecordAdapter mTxRecordAdapter;

    private static final String CURRENCY_BEAN = "currencyBean";
    private CurrencyBean.ListBean currencyBean;
    private BindAddressDialog mDialog;
    private List<BindAddressListBean.AddressListBean> addressList;


    private static final int ACTION_NONE = 0;
    private static final int ACTION_DEPOSIT = 1;

    private int pageIndex = 1;
    private int pageSize = 10;

    private QueryBindAddressPresenter mQueryBindAddressPresenter;
    private QueryTxHistoryPresenter mQueryTxHistoryPresenter;

    public static void startUp(Context context, CurrencyBean.ListBean bean) {
        ActivityStartUtils.jump(context, AssetDetailActivity.class,
                intent -> intent.putExtra(CURRENCY_BEAN, bean));
    }

    @Override
    protected void initPresenter() {
        mQueryBindAddressPresenter = registerPresenter(new QueryBindAddressPresenter(), this);
        mQueryTxHistoryPresenter = registerPresenter(new QueryTxHistoryPresenter(), this);
    }

    @Override
    protected void initData() {
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            mQueryTxHistoryPresenter.queryTxHistory(address, getCurrencyBean().tokenCurrency,
                    pageIndex * pageSize);
        }
    }

    private CurrencyBean.ListBean getCurrencyBean() {
        if (currencyBean == null) {
            currencyBean = (CurrencyBean.ListBean) getIntent().getSerializableExtra(
                    CURRENCY_BEAN);
        }
        return currencyBean;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_asset_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText(getCurrencyBean().alias);
/*        tvAmount.setText(
                TextUtils.isEmpty(getCurrencyBean().balance) ? "---" : getCurrencyBean().balance);*/
        tvAddress.setText(WalletUtils.getAddress());
        if (StringUtils.equalsIgnoreCase("PLANET", getCurrencyBean().tokenCurrency)) {
            tvDeposit.setVisibility(View.GONE);
            tvWithdraw.setVisibility(View.GONE);
            tvReceive.setVisibility(View.VISIBLE);
        } else {
            tvDeposit.setVisibility(View.VISIBLE);
            tvWithdraw.setVisibility(View.VISIBLE);
            tvReceive.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initEvents() {
        initAdapter();
        RxBindingUtils.clicks(v -> ReceiveAddressActivity.startUp(this, getCurrencyBean()), ivCode);
        RxBindingUtils.clicks(v -> ReceiveAddressActivity.startUp(this, getCurrencyBean()),
                tvReceive);
        RxBindingUtils.clicks(v -> {
            if (addressList == null) {
                if (CexDataPersistenceUtils.isLogin()) {
                    String address = WalletUtils.getAddress();
                    if (!StringUtils.isEmpty(address)) {
                        mQueryBindAddressPresenter.queryBindAddress(ACTION_DEPOSIT, address,
                                getCurrencyBean().chain, getCurrencyBean().currency);
                    }
                }
            } else {
                if (addressList.size() > 0) {
                    DepositAddressActivity.startUp(this, getCurrencyBean());
                } else {
                    mDialog = new BindAddressDialog(this, getCurrencyBean(), v1 -> {
                        mDialog.dismiss();
                        DepositBindActivity.startUp(AssetDetailActivity.this,
                                getCurrencyBean());
                    });
                    mDialog.show();
                }
            }
        }, tvDeposit);
        mTxRecordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                TxRecordDetailActivity.startUp(AssetDetailActivity.this,
                        txRecordList.get(position));
            }
        });
        RxBindingUtils.clicks(v -> WithdrawActivity.startUp(this, getCurrencyBean()), tvWithdraw);
        RxBindingUtils.clicks(v -> TransferActivity.startUp(this, getCurrencyBean()), tvTransfer);
        RxBindingUtils.clicks(v -> {
            String address = WalletUtils.getAddress();
            if (!StringUtils.isEmpty(address)) {
                CopyUtil.copy(this, "address", address);
                toast("复制成功");
            }
        }, tvCopyAddress);
    }

    private void initAdapter() {
        mTxRecordAdapter = new TxRecordAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mTxRecordAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mTxRecordAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
            }
        });
        mTxRecordAdapter.setOnLoadMoreListener(
                new BaseQuickAdapter.RequestLoadMoreListener() {
                    @Override
                    public void onLoadMoreRequested() {
                        String address = WalletUtils.getAddress();
                        if (!StringUtils.isEmpty(address)) {
                            if (txRecordList.size() == pageIndex * pageSize) {
                                //加载更多数据逻辑
                                pageIndex++;
                                mQueryTxHistoryPresenter.queryTxHistory(address,
                                        getCurrencyBean().tokenCurrency, pageIndex * pageSize,
                                        false);
                            } else {
                                mTxRecordAdapter.loadMoreEnd();
                            }
                        }
                    }
                }, mRecyclerView);
        mTxRecordAdapter.setEnableLoadMore(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CexDataPersistenceUtils.isLogin()) {
            String address = WalletUtils.getAddress();
            if (!StringUtils.isEmpty(address)) {
                mQueryBindAddressPresenter.queryBindAddress(ACTION_NONE, address,
                        getCurrencyBean().chain, getCurrencyBean().currency);
                mQueryTxHistoryPresenter.queryTxHistory(address, getCurrencyBean().tokenCurrency,
                        pageIndex * pageSize, false);
            }
        }

        String balance = CexDataPersistenceUtils.getBalance(getCurrencyBean().tokenChain,
                getCurrencyBean().tokenCurrency);
        tvBalance.setText(BigDecimalUtil.formatByDecimalValue(balance));
        tvLegalBalance.setText(
                "≈ " + PriceConvertUtil.getUsdtAmount(getCurrencyBean().alias, balance));
    }

    @Override
    public void onQueryBindAddress(int action, BindAddressListBean data) {
        addressList = new ArrayList<>();
        if (data != null) {
            addressList.addAll(data.addressList);
        }
        if (action == ACTION_DEPOSIT) {
            if (addressList.size() > 0) {
                DepositAddressActivity.startUp(this, getCurrencyBean());
            } else {
                mDialog = new BindAddressDialog(this, getCurrencyBean(), v1 -> {
                    mDialog.dismiss();
                    DepositBindActivity.startUp(AssetDetailActivity.this,
                            getCurrencyBean());
                });
                mDialog.show();
            }
        }
    }

    @Override
    public void onQueryTxHistory(TxHistoryBean data) {
        if (data != null) {
            if (data.transactions != null && data.transactions.size() > 0) {
                txRecordList.clear();
                txRecordList.addAll(data.transactions);
                mTxRecordAdapter.setNewData(txRecordList);
                mTxRecordAdapter.notifyDataSetChanged();
            } else {
                mTxRecordAdapter.setNewData(null);
                View emptyView = EmptyViewUtil.setEmpty(mRecyclerView, this, "暂无记录", 50);
                mTxRecordAdapter.setEmptyView(emptyView);
                mTxRecordAdapter.notifyDataSetChanged();
            }
        }
    }
}
