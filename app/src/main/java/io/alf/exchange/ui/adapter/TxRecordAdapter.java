package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.TxHistoryBean;
import io.alf.exchange.util.StringUtils;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DateUtil;


public class TxRecordAdapter extends
        BaseQuickAdapter<TxHistoryBean.TransactionsBean, BaseViewHolder> {

    public TxRecordAdapter() {
        super(R.layout.item_tx_record);
    }

    @Override
    protected void convert(BaseViewHolder helper, TxHistoryBean.TransactionsBean item) {
        switch (item.txType) {
            case "DEPOSIT": {
                helper.setText(R.id.tv_type, "充值");
                helper.setTextColor(R.id.tv_type,
                        mContext.getResources().getColor(R.color.color_deposit));
                helper.setBackgroundRes(R.id.tv_type, R.drawable.bg_tx_type_deposit);
                helper.setText(R.id.tv_amount,
                        "+" + BigDecimalUtil.stripTrailingZeros(item.value) + " "
                                + StringUtils.toUpperCase(
                                item.currency));
                break;
            }
            case "WITHDRAW": {
                helper.setText(R.id.tv_type, "提现");
                helper.setTextColor(R.id.tv_type,
                        mContext.getResources().getColor(R.color.color_withdraw));
                helper.setBackgroundRes(R.id.tv_type, R.drawable.bg_tx_type_withdraw);
                helper.setText(R.id.tv_amount,
                        "-" + BigDecimalUtil.stripTrailingZeros(item.value) + " "
                                + StringUtils.toUpperCase(
                                item.currency));
                break;
            }
            case "TRANSFER": {
                helper.setText(R.id.tv_type, "转账");
                helper.setTextColor(R.id.tv_type,
                        mContext.getResources().getColor(R.color.color_transfer));
                helper.setBackgroundRes(R.id.tv_type, R.drawable.bg_tx_type_transfer);
                helper.setText(R.id.tv_amount,
                        "-" + BigDecimalUtil.stripTrailingZeros(item.value) + " "
                                + StringUtils.toUpperCase(
                                item.currency));
                break;
            }
            case "RECEIPT": {
                helper.setText(R.id.tv_type, "收款");
                helper.setTextColor(R.id.tv_type,
                        mContext.getResources().getColor(R.color.color_receipt));
                helper.setBackgroundRes(R.id.tv_type, R.drawable.bg_tx_type_receipt);
                helper.setText(R.id.tv_amount,
                        "+" + BigDecimalUtil.stripTrailingZeros(item.value) + " "
                                + StringUtils.toUpperCase(
                                item.currency));
                break;
            }
        }
        helper.setText(R.id.tv_hash, item.hash);
        helper.setText(R.id.tv_time,
                DateUtil.formatDateTime(DateUtil.YMD_TIME_24_HOURS,
                        StringUtils.isEmpty(item.timestamp) ? item.sendTimestamp
                                : item.timestamp));

        switch (item.status) {
            case "1": {
                helper.setText(R.id.tv_buy_sell, "成功");
                helper.setTextColor(R.id.tv_buy_sell,
                        mContext.getResources().getColor(R.color.color_FF9B9EB));
                break;
            }
            case "0": {
                helper.setText(R.id.tv_buy_sell, "失败");
                helper.setTextColor(R.id.tv_buy_sell,
                        mContext.getResources().getColor(R.color.color_FF316FF6));
                break;
            }
            case "-1": {
                helper.setText(R.id.tv_buy_sell, "待确认");
                helper.setTextColor(R.id.tv_buy_sell,
                        mContext.getResources().getColor(R.color.color_FF9B9EB));
                break;
            }
        }

        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_symbol_title));
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_price));
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_wave_percent));
    }
}
