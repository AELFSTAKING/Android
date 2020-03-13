package io.alf.exchange.ui;

import android.content.Context;
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
import io.alf.exchange.dialog.DeleteAddressDialog;
import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.mvp.presenter.DeleteBindAddressPresenter;
import io.alf.exchange.mvp.presenter.QueryBindAddressPresenter;
import io.alf.exchange.mvp.view.DeleteBindAddressView;
import io.alf.exchange.mvp.view.QueryBindAddressView;
import io.alf.exchange.ui.adapter.ManageAddressAdapter;
import io.alf.exchange.ui.asset.DepositBindActivity;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.RxBindingUtils;

public class ManageDepositAddressActivity extends MvpActivity implements QueryBindAddressView,
        DeleteBindAddressView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_right)
    TextView tvRightTitle;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private DeleteAddressDialog mDialog;

    private ManageAddressAdapter mManageAddressAdapter;

    private static final String CURRENCY_BEAN = "currencyBean";
    private CurrencyBean.ListBean currencyBean;
    private List<BindAddressListBean.AddressListBean> addressList = new ArrayList<>();

    private QueryBindAddressPresenter mQueryBindAddressPresenter;
    private DeleteBindAddressPresenter mDeleteBindAddressPresenter;

    public static void startUp(Context context, CurrencyBean.ListBean bean) {
        ActivityStartUtils.jump(context, ManageDepositAddressActivity.class,
                intent -> intent.putExtra(CURRENCY_BEAN, bean));
    }

    private CurrencyBean.ListBean getCurrencyBean() {
        if (currencyBean == null) {
            currencyBean = (CurrencyBean.ListBean) getIntent().getSerializableExtra(
                    CURRENCY_BEAN);
        }
        return currencyBean;
    }

    @Override
    protected void initPresenter() {
        mQueryBindAddressPresenter = registerPresenter(new QueryBindAddressPresenter(), this);
        mDeleteBindAddressPresenter = registerPresenter(new DeleteBindAddressPresenter(), this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manage_deposit_addresss;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText("管理充值地址");
        tvRightTitle.setText("添加新地址");
        initAdapter();
    }

    private void initAdapter() {
        mManageAddressAdapter = new ManageAddressAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mManageAddressAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mManageAddressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (addressList != null && addressList.size() > position) {
                    BindAddressListBean.AddressListBean item = addressList.get(position);
                    //AssetDetailActivity.startUp(getContext(), item);
                }
            }
        });
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(v -> DepositBindActivity.startUp(this, getCurrencyBean()),
                tvRightTitle);
        mManageAddressAdapter.setOnItemChildClickListener(
                new BaseQuickAdapter.OnItemChildClickListener() {
                    @Override
                    public void onItemChildClick(BaseQuickAdapter adapter, View view,
                            int position) {
                        if (view.getId() == R.id.iv_delete) {
                            mDialog = new DeleteAddressDialog(ManageDepositAddressActivity.this,
                                    v -> {
                                        mDialog.dismiss();
                                        BindAddressListBean.AddressListBean bean =
                                                addressList.get(position);
                                        String address = WalletUtils.getAddress();
                                        if (!StringUtils.isEmpty(address)) {
                                            mDeleteBindAddressPresenter.deleteBindAddress(
                                                    address,
                                                    bean.chain,
                                                    bean.currency,
                                                    bean.address);
                                        }
                                    });
                            mDialog.show();
                        }
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CexDataPersistenceUtils.isLogin()) {
            String address = WalletUtils.getAddress();
            if (!StringUtils.isEmpty(address)) {
                mQueryBindAddressPresenter.queryBindAddress(0, address,
                        getCurrencyBean().chain, getCurrencyBean().currency);
            }
        }
    }

    @Override
    public void onQueryBindAddress(int action, BindAddressListBean data) {
        if (data != null) {
            addressList.clear();
            addressList.addAll(data.addressList);
            mManageAddressAdapter.setNewData(addressList);
            mManageAddressAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDeleteBindAddress(boolean success, BindAddressListBean data) {
        if (success) {
            addressList.clear();
            addressList.addAll(data.addressList);
            mManageAddressAdapter.setNewData(addressList);
            mManageAddressAdapter.notifyDataSetChanged();
        }
    }
}
