package io.alf.exchange.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.TxHistoryBean;
import io.alf.exchange.mvp.bean.TxInfoBean;
import io.alf.exchange.mvp.presenter.TxRecordDetailPresenter;
import io.alf.exchange.mvp.view.TxRecordDetailView;
import io.alf.exchange.util.SpannableUtils;
import io.alf.exchange.util.StringUtils;
import io.tick.base.mvp.MvpActivity;
import io.tick.base.util.ActivityStartUtils;
import io.tick.base.util.DateUtil;

public class TxRecordDetailActivity extends MvpActivity implements TxRecordDetailView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_buy_sell)
    TextView tvStatus;
    @BindView(R.id.tv_send_address)
    TextView tvSendAddress;
    @BindView(R.id.tv_receive_address)
    TextView tvReceiveAddress;
    @BindView(R.id.tv_fee)
    TextView tvFee;
    @BindView(R.id.tv_block_height)
    TextView tvBlockHeight;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_txid)
    TextView tvTxID;


    private static final String BEAN = "bean";
    private TxHistoryBean.TransactionsBean bean;
    private TxRecordDetailPresenter mTxRecordDetailPresenter;

    public static void startUp(Context context, TxHistoryBean.TransactionsBean bean) {
        ActivityStartUtils.jump(context, TxRecordDetailActivity.class,
                intent -> intent.putExtra(BEAN, bean));
    }

    private TxHistoryBean.TransactionsBean getBean() {
        if (bean == null) {
            bean = (TxHistoryBean.TransactionsBean) getIntent().getSerializableExtra(BEAN);
        }
        return bean;
    }

    @Override
    protected void initPresenter() {
        mTxRecordDetailPresenter = registerPresenter(new TxRecordDetailPresenter(), this);
    }

    @Override
    protected void initData() {
        mTxRecordDetailPresenter.queryTxInfo("eth", getBean().currency, getBean().hash);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tx_record_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(view -> onBackPressed());
        tvTitle.setText(getTxTitle());
        tvAmount.setText(getTxAmount());
        tvStatus.setText(getTxType() + getTxStatus());
        tvSendAddress.setText(getBean().sender);
        if (StringUtils.equals(getBean().txType, "WITHDRAW")) {
            tvReceiveAddress.setText(getBean().withdrawReceiver);
        } else {
            tvReceiveAddress.setText(getBean().receiver);
        }
        setBlockHeightAndTime();
        tvTxID.setMovementMethod(LinkMovementMethod.getInstance());
        SpannableUtils.setHtmlTextView(tvTxID, getBean().hash, getBean().url);
    }

    @Override
    protected void initEvents() {

    }

    private void setBlockHeightAndTime() {
        switch (getBean().status) {
            case "1": {
                tvBlockHeight.setText(getBean().blockHeight);
                tvTime.setText(DateUtil.formatDateTime(DateUtil.FMT_YYYY_MM_DD_HH_MM,
                        StringUtils.isEmpty(getBean().timestamp) ? getBean().sendTimestamp
                                : getBean().timestamp));
                break;
            }
            case "0": {
                tvBlockHeight.setText(getBean().blockHeight);
                tvTime.setText(DateUtil.formatDateTime(DateUtil.FMT_YYYY_MM_DD_HH_MM,
                        StringUtils.isEmpty(getBean().timestamp) ? getBean().sendTimestamp
                                : getBean().timestamp));
                break;
            }
            case "-1": {
                tvBlockHeight.setText("");
                tvTime.setText("");
                break;
            }
        }
    }

    private String getTxTitle() {
        String title = "";
        switch (getBean().txType) {
            case "DEPOSIT": {
                title = StringUtils.toUpperCase(getBean().currency) + "充值";
                break;
            }
            case "WITHDRAW": {
                title = StringUtils.toUpperCase(getBean().currency) + "提现";
                break;
            }
            case "TRANSFER": {
                title = StringUtils.toUpperCase(getBean().currency) + "转账";
                break;
            }
            case "RECEIPT": {
                title = StringUtils.toUpperCase(getBean().currency) + "收款";
                break;
            }
        }
        return title;
    }

    private String getTxAmount() {
        String amount = "";
        switch (getBean().txType) {
            case "DEPOSIT": {
                amount = "+" + getBean().value;
                break;
            }
            case "WITHDRAW": {
                amount = "-" + getBean().value;
                break;
            }
            case "TRANSFER": {
                amount = "-" + getBean().value;
                break;
            }
            case "RECEIPT": {
                amount = "+" + getBean().value;
                break;
            }
        }
        return amount;
    }

    private String getTxType() {
        String type = "";
        switch (getBean().txType) {
            case "DEPOSIT": {
                type = StringUtils.toUpperCase(getBean().currency) + "充值";
                break;
            }
            case "WITHDRAW": {
                type = StringUtils.toUpperCase(getBean().currency) + "提现";
                break;
            }
            case "TRANSFER": {
                type = StringUtils.toUpperCase(getBean().currency) + "转账";
                break;
            }
            case "RECEIPT": {
                type = StringUtils.toUpperCase(getBean().currency) + "收款";
                break;
            }
        }

        return type;
    }

    private String getTxStatus() {
        String status = "";
        switch (getBean().status) {
            case "1": {
                status = "成功";
                break;
            }
            case "0": {
                status = "失败";
                break;
            }
            case "-1": {
                status = "待确认";
                break;
            }
        }
        return status;
    }


    @Override
    public void onQueryTxInfo(boolean success, TxInfoBean data) {
        if (success) {
            if (data != null) {
                tvFee.setText(data.fee + " ETH");
            }
        }
    }
}
