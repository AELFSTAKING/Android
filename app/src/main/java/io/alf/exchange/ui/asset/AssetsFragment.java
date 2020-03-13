package io.alf.exchange.ui.asset;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.immersionbar.ImmersionBar;

import org.consenlabs.tokencore.wallet.Identity;
import org.consenlabs.tokencore.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.dialog.PasswordDialog;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.mvp.bean.RewardNoticeBean;
import io.alf.exchange.mvp.bean.TotalAssetBean;
import io.alf.exchange.mvp.presenter.IdentityPresenter;
import io.alf.exchange.mvp.presenter.QueryAssetsPresenter;
import io.alf.exchange.mvp.presenter.QueryBalancePresenter;
import io.alf.exchange.mvp.presenter.QueryCurrencyListPresenter;
import io.alf.exchange.mvp.presenter.RewardNoticePresenter;
import io.alf.exchange.mvp.view.QueryAssetsView;
import io.alf.exchange.mvp.view.QueryBalanceView;
import io.alf.exchange.mvp.view.QueryCurrencyListView;
import io.alf.exchange.mvp.view.RewardNoticeView;
import io.alf.exchange.ui.RewardActivity;
import io.alf.exchange.ui.adapter.AssetAdapter;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.Validator;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpFragment;
import io.tick.base.ui.ActionSheet;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.RxBindingUtils;

public class AssetsFragment extends MvpFragment implements QueryCurrencyListView, QueryBalanceView,
        QueryAssetsView, RewardNoticeView {

    @BindView(R.id.tv_account_name)
    TextView tvAccountName;
    @BindView(R.id.tv_total_legal_currency_asset)
    TextView tvTotalAmount;
    @BindView(R.id.iv_hide_asset)
    ImageView ivHideAsset;
    @BindView(R.id.iv_setting)
    ImageView tvSettings;
    @BindView(R.id.rl_notice)
    RelativeLayout rlNotice;
    @BindView(R.id.tv_reward_notice)
    TextView tvRewardNotice;
    @BindView(R.id.ll_set_account)
    LinearLayout llSetUpAccount;
    @BindView(R.id.tv_create_account)
    TextView tvCreateAccount;
    @BindView(R.id.tv_import_account)
    TextView tvImportAccount;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private PasswordDialog mDialog;
    private AssetAdapter mAssetAdapter;
    private List<CurrencyBean.ListBean> assetData = new ArrayList<>();

    private QueryCurrencyListPresenter mQueryCurrencyListPresenter;
    private IdentityPresenter mIdentityPresenter;
    private QueryBalancePresenter mQueryBalancePresenter;
    private QueryAssetsPresenter mQueryAssetsPresenter;
    private RewardNoticePresenter mRewardNoticePresenter;
    private static long lastUpdateTime = 0;

    @Override
    protected void initPresenter() {
        mQueryCurrencyListPresenter = registerPresenter(new QueryCurrencyListPresenter(), this);
        mQueryBalancePresenter = registerPresenter(new QueryBalancePresenter(), this);
        mIdentityPresenter = registerPresenter(new IdentityPresenter(), this);
        mQueryAssetsPresenter = registerPresenter(new QueryAssetsPresenter(), this);
        mQueryCurrencyListPresenter.queryCurrencyList();
        mRewardNoticePresenter = registerPresenter(new RewardNoticePresenter(), this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_assets;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initAssets();
        // 初始化隐藏资产眼睛图标
        ivHideAsset.setImageResource(
                CexDataPersistenceUtils.isHideAssets() ? R.mipmap.me_icon_eye_pre
                        : R.mipmap.me_icon_eye_nor);
    }

    @Override
    public void onResume() {
        super.onResume();
        initUIByID();
        updateData();
        queryBalance();
        queryAssets();
        queryReward();
    }

    @Override
    protected void onVisibleToUser() {
        super.onVisibleToUser();
        queryBalance();
        queryAssets();
        queryReward();
    }

    private void queryAssets() {
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            long currentTime = System.currentTimeMillis();
            // 10s 刷新一次
            if ((currentTime - lastUpdateTime) > 10 * 60 * 1000) {
                lastUpdateTime = currentTime;
                // TODO: 2019/1/30
                mQueryAssetsPresenter.queryAssetList(address);
            }
        }
    }

    private void queryReward() {
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            mRewardNoticePresenter.queryRewardNotice(address);
        }
    }

    private void initUIByID() {
        Identity identity = mIdentityPresenter.getIdentity();
        if (identity != null) {
            tvAccountName.setText(identity.getMetadata().getName());
            tvSettings.setVisibility(View.VISIBLE);
            llSetUpAccount.setVisibility(View.GONE);
            rlNotice.setVisibility(View.VISIBLE);
        } else {
            tvSettings.setVisibility(View.GONE);
            llSetUpAccount.setVisibility(View.VISIBLE);
            rlNotice.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initEvents() {
        // 隐藏/显示资产功能
        RxBindingUtils.clicks(aVoid -> {
            boolean hideAsset = !CexDataPersistenceUtils.isHideAssets();
            CexDataPersistenceUtils.setHideAssets(hideAsset);
            ivHideAsset.setImageResource(
                    hideAsset ? R.mipmap.me_icon_eye_pre : R.mipmap.me_icon_eye_nor);
            updateData();
        }, ivHideAsset);
        RxBindingUtils.clicks(
                v -> ActivityStartUtils.jump(getContext(), CreateAccountActivity.class),
                tvCreateAccount);
        RxBindingUtils.clicks(
                v -> ActivityStartUtils.jump(getContext(), ImportAccountActivity.class),
                tvImportAccount);
        RxBindingUtils.clicks(v -> ActionSheet.createBuilder(getContext(), getFragmentManager())
                .setCancelButtonTitle(R.string.cancel)
                .setOtherButtonTitles(getResources().getStringArray(R.array.asset_menu))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.SampleActionSheetListener() {
                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0: {
                                ActivityStartUtils.jump(getContext(), ModifyAccountActivity.class);
                                break;
                            }
                            case 1: {
                                ActivityStartUtils.jump(getContext(), ResetPasswordActivity.class);
                                break;
                            }
                            case 2: {
                                mDialog = new PasswordDialog(getContext(), v -> {
                                    TextView passwordView = (TextView) v;
                                    if (Validator.checkPassword(getContext(), passwordView)) {
                                        Wallet wallet = WalletUtils.getWallet();
                                        if (wallet != null) {
                                            ExportAccountActivity.startUp(getContext(),
                                                    wallet.getAddress(),
                                                    wallet.exportPrivateKey(
                                                            passwordView.getText().toString()));
                                        }
                                        mDialog.dismiss();
                                    }
                                });
                                mDialog.show();
                                break;
                            }
                        }
                    }
                }).show(), tvSettings);

        RxBindingUtils.clicks(v -> ActivityStartUtils.jump(getContext(), RewardActivity.class),
                rlNotice);
    }

    private void initAssets() {
        mAssetAdapter = new AssetAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAssetAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mAssetAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (assetData != null && assetData.size() > position) {
                    Identity identity = mIdentityPresenter.getIdentity();
                    if (identity != null) {
                        CurrencyBean.ListBean item = assetData.get(position);
                        AssetDetailActivity.startUp(getContext(), item);
                    } else {
                        toast("请先创建/导入充值账户");
                    }
                }
            }
        });

        List<CurrencyBean.ListBean> currencyList = CexDataPersistenceUtils.getSupportCurrencies();
        if (currencyList != null && currencyList.size() > 0) {
            assetData.clear();
            assetData.addAll(currencyList);
            mAssetAdapter.setNewData(assetData);
            mAssetAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onQueryCurrencyList(CurrencyBean data) {
        if (data != null) {
            assetData.clear();
            assetData.addAll(data.list);
            mAssetAdapter.setNewData(assetData);
            mAssetAdapter.notifyDataSetChanged();
            queryBalance();
        }
    }

    /**
     * 查询支持币种余额
     */
    private void queryBalance() {
        List<CurrencyBean.ListBean> currencyList = CexDataPersistenceUtils.getSupportCurrencies();
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address) && currencyList != null && currencyList.size() > 0) {
            for (CurrencyBean.ListBean bean : currencyList) {
                mQueryBalancePresenter.queryBalance(bean.tokenChain, bean.tokenCurrency, address);
            }
        }
    }

    @Override
    public void onQueryBalance(String chain, String currency, String amount) {
        if (!TextUtils.isEmpty(currency)) {
            for (CurrencyBean.ListBean item : assetData) {
                if (currency.equalsIgnoreCase(item.tokenCurrency)
                        && chain.equalsIgnoreCase(item.tokenChain)) {
                    updateData();
                    break;
                }
            }
        }
    }

    private void updateData() {
        // 1.计算总金额
        updateTotalAsset();

        // 2.刷新资产列表
        mAssetAdapter.setNewData(assetData);
        mAssetAdapter.notifyDataSetChanged();
    }

    private void updateTotalAsset() {
        if (CexDataPersistenceUtils.isHideAssets()) {
            tvTotalAmount.setText("***");
        } else {
            TotalAssetBean bean = CexDataPersistenceUtils.getTotalAssetBean();
            if (bean != null && !StringUtils.isEmpty(bean.totalUsdtAsset)) {
                tvTotalAmount.setText(BigDecimalUtil.formatByDecimalValue(bean.totalUsdtAsset, 4));
            } else {
                tvTotalAmount.setText("---");
            }
        }
    }

    @Override
    public void initImmersionBar() {
        ImmersionBar.with(this).keyboardEnable(true).init();
    }

    @Override
    public void onQueryAssetList(TotalAssetBean data) {
        updateData();
    }

    @Override
    public void onQueryRewardNotice(boolean success, RewardNoticeBean data) {
        if (success) {
            if (data != null) {
                String sendedReward = BigDecimalUtil.formatByDecimalValue(data.sendedReward, 2);
                if (!StringUtils.isEmpty(sendedReward) && sendedReward.length() > 8) {
                    sendedReward = StringUtils.ellipsize(sendedReward, 3, 0);
                }
                String unsendReward = BigDecimalUtil.formatByDecimalValue(data.unsendReward, 2);
                if (!StringUtils.isEmpty(unsendReward) && unsendReward.length() > 8) {
                    unsendReward = StringUtils.ellipsize(unsendReward, 3, 0);
                }
                tvRewardNotice.setText(
                        String.format("已收奖励 %s %s 待发奖励 %s %s ",
                                sendedReward,
                                data.rewardCurrency,
                                unsendReward,
                                data.rewardCurrency));
            } else {
                tvRewardNotice.setText("已收奖励 --  待发奖励 --");
            }
        } else {
            tvRewardNotice.setText("已收奖励 --  待发奖励 --");
        }
    }
}
