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
import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.mvp.bean.DepositAddressBean;
import io.alf.exchange.mvp.presenter.QueryBindAddressPresenter;
import io.alf.exchange.mvp.presenter.QueryDepositAddressPresenter;
import io.alf.exchange.mvp.view.QueryBindAddressView;
import io.alf.exchange.mvp.view.QueryDepositAddressView;
import io.alf.exchange.ui.ManageDepositAddressActivity;
import io.alf.exchange.ui.adapter.BindAddressAdapter;
import io.alf.exchange.util.RxQRCode;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.CopyUtil;
import io.tick.base.util.DensityUtil;
import io.tick.base.util.RxBindingUtils;

public class DepositAddressActivity extends MvpActivity implements QueryBindAddressView,
        QueryDepositAddressView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_code)
    ImageView ivCode;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_copy)
    TextView tvCopy;
    @BindView(R.id.tv_notice_1)
    TextView tvNotice1;
    @BindView(R.id.tv_notice_2)
    TextView tvNotice2;
    @BindView(R.id.tv_notice_3)
    TextView tvNotice3;
    @BindView(R.id.tv_manage_address)
    TextView tvManageAddress;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;


    private static final String CURRENCY_BEAN = "currencyBean";
    private CurrencyBean.ListBean currencyBean;
    private BindAddressAdapter mBindAddressAdapter;
    private List<BindAddressListBean.AddressListBean> addressList = new ArrayList<>();

    private QueryBindAddressPresenter mQueryBindAddressPresenter;
    private QueryDepositAddressPresenter mQueryDepositAddressPresenter;


    public static void startUp(Context context, CurrencyBean.ListBean bean) {
        ActivityStartUtils.jump(context, DepositAddressActivity.class,
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
        mQueryDepositAddressPresenter = registerPresenter(new QueryDepositAddressPresenter(), this);
    }

    @Override
    protected void initData() {
        mQueryDepositAddressPresenter.queryDepositAddress(getCurrencyBean().currency);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            mQueryBindAddressPresenter.queryBindAddress(0, address,
                    getCurrencyBean().chain, getCurrencyBean().currency);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_deposit_addresss;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText(getCurrencyBean().alias + "跨链充值");
        tvNotice1.setText(
                String.format("1、该地址只支持%s跨链充值，请不要使用没有绑定的%s地址进行充值。", getCurrencyBean().currency,
                        getCurrencyBean().currency));
        if (StringUtils.equalsIgnoreCase(getCurrencyBean().currency, "BTC")) {
            tvNotice2.setText(String.format("2、%s跨链充值只允许使用绑定地址的UTXO进行充值，否则充值将无法正常入账。",
                    getCurrencyBean().currency));
            tvNotice3.setVisibility(View.VISIBLE);
            tvNotice3.setText("3、该地址不支持通过交易所账户以提币的方式进行充值。");
        } else {
            tvNotice2.setText("2、该地址不支持通过交易所账户以提币的方式进行充值。");
            tvNotice3.setVisibility(View.GONE);
        }
        initAdapter();
    }

    private void initAdapter() {
        mBindAddressAdapter = new BindAddressAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mBindAddressAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mBindAddressAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
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
        RxBindingUtils.clicks(v -> {
            CopyUtil.copy(this, "address", WalletUtils.getAddress());
            toast("复制成功");
        }, tvCopy);
        RxBindingUtils.clicks(v -> ManageDepositAddressActivity.startUp(this, getCurrencyBean()),
                tvManageAddress);
    }

    @Override
    public void onQueryBindAddress(int action, BindAddressListBean data) {
        if (data != null) {
            addressList.clear();
            addressList.addAll(data.addressList);
            mBindAddressAdapter.setNewData(addressList);
            mBindAddressAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onQueryDepositAddress(String currency, DepositAddressBean data) {
        tvAddress.setText(data.address);
        ivCode.setImageBitmap(RxQRCode.createQRCode(data.address, DensityUtil.dp2px(200),
                DensityUtil.dp2px(200)));
    }
}
