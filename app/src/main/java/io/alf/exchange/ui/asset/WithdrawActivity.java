package io.alf.exchange.ui.asset;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.Toolbar;

import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.BindView;
import io.alf.exchange.Constant;
import io.alf.exchange.R;
import io.alf.exchange.dialog.ErrorDialog;
import io.alf.exchange.dialog.PasswordDialog;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.mvp.bean.FeeBean;
import io.alf.exchange.mvp.bean.WithdrawOrderBean;
import io.alf.exchange.mvp.presenter.QueryBalancePresenter;
import io.alf.exchange.mvp.presenter.QueryFeePresenter;
import io.alf.exchange.mvp.presenter.QueryPricePresenter;
import io.alf.exchange.mvp.presenter.WithdrawPresenter;
import io.alf.exchange.mvp.presenter.eth.tx.EthCreateTx;
import io.alf.exchange.mvp.presenter.eth.tx.EthSendTx;
import io.alf.exchange.mvp.view.QueryBalanceView;
import io.alf.exchange.mvp.view.QueryFeeView;
import io.alf.exchange.mvp.view.QueryPriceView;
import io.alf.exchange.mvp.view.WithdrawView;
import io.alf.exchange.sample.SampleObserver;
import io.alf.exchange.ui.ScannerActivity;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.DecimalDigitsInputFilter;
import io.alf.exchange.util.PriceConvertUtil;
import io.alf.exchange.util.QRCodeHelper;
import io.alf.exchange.util.SignTxUtil;
import io.alf.exchange.util.StringUtils;
import io.alf.exchange.util.Validator;
import io.alf.exchange.util.WalletUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.RxBindingUtils;

public class WithdrawActivity extends MvpActivity implements QueryBalanceView,
        QueryFeeView, WithdrawView, QueryPriceView, QRCodeHelper.OnQRCodeGetListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_currency)
    TextView tvCurrency;
    @BindView(R.id.et_amount)
    EditText etAmount;
    @BindView(R.id.tv_all)
    TextView tvAll;
    @BindView(R.id.tv_legal_amount)
    TextView tvLegalAmount;
    @BindView(R.id.tv_balance)
    TextView tvBalance;
    @BindView(R.id.tv_receive_address_title)
    TextView getTvReceiveAddressTitle;
    @BindView(R.id.tv_receive_address)
    TextView tvReceiveAddress;
    @BindView(R.id.tv_send_address)
    TextView tvSendAddress;
    @BindView(R.id.tv_eth_balance)
    TextView tvEthBalance;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.seekbar)
    AppCompatSeekBar seekBar;
    @BindView(R.id.tv_advice_fee)
    TextView tvAdviceFee;
    @BindView(R.id.tv_submit)
    TextView tvSubmit;
    @BindView(R.id.iv_scan)
    ImageView ivScan;
    @BindView(R.id.tv_btc_notice)
    TextView tvBtcNotice;

    private PasswordDialog mPasswordDialog;

    private static final double MAX_PROGRESS = 10000d;

    private static final String CURRENCY_BEAN = "currencyBean";
    private CurrencyBean.ListBean currencyBean;
    private FeeBean mFeeBean;
    private String fee;

    private QueryBalancePresenter mQueryBalancePresenter;
    private QueryFeePresenter mQueryFeePresenter;
    private WithdrawPresenter mWithdrawPresenter;
    private QueryPricePresenter mQueryPricePresenter;

    private String[] mPerms = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public static void startUp(Context context, CurrencyBean.ListBean bean) {
        ActivityStartUtils.jump(context, WithdrawActivity.class,
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
        mQueryBalancePresenter = registerPresenter(new QueryBalancePresenter(), this);
        mQueryFeePresenter = registerPresenter(new QueryFeePresenter(), this);
        mWithdrawPresenter = registerPresenter(new WithdrawPresenter(), this);
        mQueryPricePresenter = registerPresenter(new QueryPricePresenter(), this);
    }

    @Override
    protected void initData() {
        mQueryPricePresenter.queryUsdtPrice("eth");
        mQueryFeePresenter.queryWithdrawFee(getCurrencyBean().tokenChain,
                getCurrencyBean().tokenCurrency);
        String address = WalletUtils.getAddress();
        if (!StringUtils.isEmpty(address)) {
            mQueryBalancePresenter.queryBalance(getCurrencyBean().tokenChain,
                    getCurrencyBean().tokenCurrency, address);
            mQueryBalancePresenter.queryBalance("eth",
                    "eth", address);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_withdraw;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText(getCurrencyBean().alias + "提现");
        seekBar.setMax((int) MAX_PROGRESS);
        tvCurrency.setText(getCurrencyBean().alias);
        String balance = CexDataPersistenceUtils.getBalance(getCurrencyBean().tokenChain,
                getCurrencyBean().tokenCurrency);
        tvBalance.setText("当前余额:" + balance);
        tvLegalAmount.setText(
                "≈" + PriceConvertUtil.getUsdtAmount(getCurrencyBean().alias, "0"));
        tvReceiveAddress.setHint(String.format("输入%s地址", getCurrencyBean().currency));
        tvSendAddress.setText(WalletUtils.getAddress());
        tvEthBalance.setText(
                String.format("ETH余额：%s ETH", CexDataPersistenceUtils.getBalance("eth", "eth")));
        tvBtcNotice.setVisibility(
                StringUtils.equals(getCurrencyBean().alias, "S-BTC") ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void initEvents() {
        RxBindingUtils.clicks(aVoid -> {
            RxPermissions rxPermissions = new RxPermissions(this);
            rxPermissions.request(mPerms)
                    .subscribe(new SampleObserver<Boolean>() {
                        @Override
                        public void onNext(Boolean value) {
                            QRCodeHelper.openQRCodeScanner(WithdrawActivity.this,
                                    ScannerActivity.class);
                        }
                    });
        }, ivScan);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mFeeBean != null) {
                    try {
                        String temp = BigDecimalUtil.sub(mFeeBean.fastGasPrice,
                                mFeeBean.slowGasPrice);
                        temp = BigDecimalUtil.mul(temp, String.valueOf(progress / MAX_PROGRESS));
                        temp = BigDecimalUtil.add(mFeeBean.slowGasPrice, temp);
                        temp = BigDecimalUtil.mul(temp, mFeeBean.gasLimit);
                        temp = BigDecimalUtil.mul(temp, String.valueOf(Math.pow(10, -18)));
                        fee = BigDecimalUtil.stripTrailingZeros(temp);
                        tvFee.setText(String.format("%s ETH ～ %s", fee,
                                PriceConvertUtil.getUsdtAmount("ETH",
                                        fee)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        RxBindingUtils.clicks(v -> {
            String balance = CexDataPersistenceUtils.getBalance(getCurrencyBean().tokenChain,
                    getCurrencyBean().tokenCurrency);
            etAmount.setText(balance);
        }, tvAll);

        RxBindingUtils.clicks(v -> {
            if (validate()) {
                mPasswordDialog = new PasswordDialog(this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView passwordView = (TextView) v;
                        if (Validator.checkPassword(WithdrawActivity.this, passwordView)) {
                            mPasswordDialog.dismiss();
                            try {
                                String gasPrice = BigDecimalUtil.mul(fee,
                                        String.valueOf(Math.pow(10, 18)));
                                gasPrice = BigDecimalUtil.div(gasPrice, mFeeBean.gasLimit, 0);
                                String takerSignature = generateTakerSignature();
                                mWithdrawPresenter.createWithdrawOrder(
                                        getCurrencyBean().tokenChain,
                                        getCurrencyBean().tokenCurrency,
                                        etAmount.getText().toString(),
                                        tvSendAddress.getText().toString(),
                                        tvReceiveAddress.getText().toString(),
                                        takerSignature,
                                        gasPrice,
                                        passwordView.getText().toString()
                                );
                            } catch (Exception e) {
                                e.printStackTrace();
                                toast("参数错误，创建交易失败");
                            }
                        }
                    }
                });
                mPasswordDialog.show();
            }
        }, tvSubmit);

        etAmount.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(etAmount.getText().toString())) {
                    tvLegalAmount.setText(
                            "≈" + PriceConvertUtil.getUsdtAmount(getCurrencyBean().alias, "0"));
                } else {
                    try {
                        String amount = etAmount.getText().toString();
                        tvLegalAmount.setText(
                                "≈" + PriceConvertUtil.getUsdtAmount(getCurrencyBean().alias,
                                        amount));
                    } catch (Exception e) {
                        tvLegalAmount.setText(
                                "≈" + PriceConvertUtil.getUsdtAmount(getCurrencyBean().alias, "0"));
                    }
                }
            }
        });

        etAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(8)});
    }

    private String generateTakerSignature() {
        String kofoSecret = Constant.KOFO_PRIKEY;
        String makerAmount = etAmount.getText().toString();
        String makerChain = getCurrencyBean().chain;
        String makerCurrency = getCurrencyBean().currency;
        String takerAddress = tvSendAddress.getText().toString();
        String takerAmount = etAmount.getText().toString();
        String takerChain = getCurrencyBean().tokenChain;
        String takerCounterChainAddress = tvReceiveAddress.getText().toString();
        String takerCurrency = getCurrencyBean().tokenCurrency;
        String takerKofoId = Constant.KOFO_ID;
        return WalletUtils.takerPreSignStr(
                kofoSecret,
                makerAmount,
                makerChain,
                makerCurrency,
                takerAddress,
                takerAmount,
                takerChain,
                takerCounterChainAddress,
                takerCurrency,
                takerKofoId);
    }

    private boolean validate() {
        String amount = etAmount.getText().toString();
        String balance = CexDataPersistenceUtils.getBalance(getCurrencyBean().tokenChain,
                getCurrencyBean().tokenCurrency);
        if (StringUtils.isEmpty(amount)) {
            new ErrorDialog(this, "输入提现金额").show();
            return false;
        } else if (BigDecimalUtil.isGreater(amount, balance)) {
            new ErrorDialog(this, "提现金额不能超过余额").show();
            return false;
        }
        return Validator.checkAddress(this, tvReceiveAddress)
                && Validator.checkAddress(this, tvSendAddress);
    }

    @Override
    public void onQueryBalance(String chain, String currency, String amount) {
        if (StringUtils.equals("eth", chain) && StringUtils.equals("eth", currency)) {
            tvEthBalance.setText(
                    String.format("ETH余额：%s ETH", amount));
        } else if (StringUtils.equals(getCurrencyBean().tokenChain, chain)
                && StringUtils.equals(getCurrencyBean().tokenCurrency, currency)) {
            tvBalance.setText("当前余额:" + amount);
        }
    }

    private void updateSeekBarProgress() {
        if (mFeeBean != null) {
            try {
                String t1 = BigDecimalUtil.mul(mFeeBean.fee, String.valueOf(Math.pow(10, 18)));
                t1 = BigDecimalUtil.div(t1, mFeeBean.gasLimit, 6);
                t1 = BigDecimalUtil.sub(t1, mFeeBean.slowGasPrice);
                String t2 = BigDecimalUtil.sub(mFeeBean.fastGasPrice, mFeeBean.slowGasPrice);
                String progress = BigDecimalUtil.div(t1, t2, 6);
                progress = BigDecimalUtil.mul(progress, String.valueOf(MAX_PROGRESS), 0);
                int p = Integer.parseInt(progress);
                seekBar.setProgress(p);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onQueryFee(FeeBean data) {
        if (data != null) {
            mFeeBean = data;
            fee = data.fee;
            tvAdviceFee.setText(String.format("建议：%s ETH ～ %s", mFeeBean.fee,
                    PriceConvertUtil.getUsdtAmount("ETH", mFeeBean.fee)));
            updateSeekBarProgress();
        }
    }

    @Override
    public void onCreateWithdrawOrder(boolean success, WithdrawOrderBean data, String sender,
            String gasPrice, String password) {
        if (success) {
            if (data != null) {
                mWithdrawPresenter.createApproveTx(
                        data.settlementId,
                        getCurrencyBean().tokenChain,
                        getCurrencyBean().tokenCurrency,
                        data.takerAmount,
                        sender,
                        gasPrice,
                        data.withdrawNo,
                        password);
            }

        }
    }

    @Override
    public void onCreateApproveTx(boolean success, EthCreateTx data, String settlementId,
            String chain, String currency, String withdrawNo, String password) {
        if (success) {
            if (data != null) {
                String signedRawTransaction = SignTxUtil.signTx(data.rawTransaction, password);
                mWithdrawPresenter.sendApproveTx(
                        settlementId,
                        chain,
                        currency,
                        signedRawTransaction,
                        withdrawNo);
            }
        }
    }

    @Override
    public void onSendApproveTx(boolean success, String settlementId, EthSendTx data,
            String withdrawNo) {
        if (success) {
            mWithdrawPresenter.takerSubmitApproveCallback(settlementId,
                    getCurrencyBean().tokenChain, data.txHash, "");
            //mWithdrawPresenter.withdrawCallback(withdrawNo, data.txHash);
            toast("发送交易成功");
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QRCodeHelper.onActivityResult(requestCode, resultCode, data, WithdrawActivity.this);
    }

    @Override
    public void onGetQRCode(String result) {
        tvReceiveAddress.setText(result);
    }

    @Override
    public void onQueryUsdtPrice(String currency, String price) {
        if (mFeeBean != null) {
            tvAdviceFee.setText(String.format("建议：%s ETH ～ %s", mFeeBean.fee,
                    PriceConvertUtil.getUsdtAmount("ETH", mFeeBean.fee)));
            try {
                String temp = BigDecimalUtil.sub(mFeeBean.fastGasPrice,
                        mFeeBean.slowGasPrice);
                temp = BigDecimalUtil.mul(temp,
                        String.valueOf(seekBar.getProgress() / MAX_PROGRESS));
                temp = BigDecimalUtil.add(mFeeBean.slowGasPrice, temp);
                temp = BigDecimalUtil.mul(temp, mFeeBean.gasLimit);
                temp = BigDecimalUtil.mul(temp, String.valueOf(Math.pow(10, -18)));
                fee = BigDecimalUtil.stripTrailingZeros(temp);
                tvFee.setText(String.format("%s ETH ～ %s", fee,
                        PriceConvertUtil.getUsdtAmount("ETH",
                                fee)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
