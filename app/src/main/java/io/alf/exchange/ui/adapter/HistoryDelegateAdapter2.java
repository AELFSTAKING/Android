package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.bean.OrderAction;
import io.alf.exchange.bean.OrderType;
import io.alf.exchange.mvp.bean.OrderHistoryBean;
import io.alf.exchange.util.StringUtils;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DateUtil;


public class HistoryDelegateAdapter2 extends
        BaseQuickAdapter<OrderHistoryBean.ListBean, BaseViewHolder> {
    public HistoryDelegateAdapter2() {
        super(R.layout.item_history_delegate);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderHistoryBean.ListBean item) {
        StringBuffer buffer = new StringBuffer();
        if (item.orderType.equals(OrderType.LMT)) {
            buffer.append(mContext.getString(R.string.limit_price));
        } else if (item.orderType.equals(OrderType.MKT)) {
            buffer.append(mContext.getString(R.string.cur_best_price));
        }
        switch (item.action) {
            case OrderAction.BUY:
                helper.setTextColor(R.id.tv_buy_sell,
                        mContext.getResources().getColor(R.color.color_increase));
                helper.setBackgroundRes(R.id.tv_buy_sell, R.drawable.bg_increase_stroke);
                helper.setText(R.id.tv_buy_sell,
                        buffer.append(mContext.getString(R.string.buy)).toString());
                break;
            case OrderAction.SELL:
                helper.setTextColor(R.id.tv_buy_sell,
                        mContext.getResources().getColor(R.color.color_decrease));
                helper.setText(R.id.tv_buy_sell,
                        buffer.append(mContext.getString(R.string.sell)).toString());
                helper.setBackgroundRes(R.id.tv_buy_sell, R.drawable.bg_decrease_stroke);
                break;
        }

        helper.setText(R.id.tv_symbol_title, item.symbol);
        helper.setText(R.id.tv_date,
                DateUtil.formatDateTime(DateUtil.FMT_HH_MM_MM_DD, item.utcCreate));

        helper.setText(R.id.tv_price_title, String.format("委托价(%s)", item.counterCurrency));
        helper.setText(R.id.tv_price, BigDecimalUtil.stripTrailingZeros(item.priceLimit));

        helper.setText(R.id.tv_quantity_title, String.format("委托量(%s)", item.currency));
        helper.setText(R.id.tv_quantity, BigDecimalUtil.stripTrailingZeros(item.quantity));

        helper.setText(R.id.tv_fee_title, String.format("手续费(%s)", item.feeCurrency));
        helper.setText(R.id.tv_fee, BigDecimalUtil.stripTrailingZeros(item.fee));

        helper.setText(R.id.tv_total_amount_title, String.format("成交总额(%s)", item.counterCurrency));
        helper.setText(R.id.tv_total_amount, BigDecimalUtil.stripTrailingZeros(
                BigDecimalUtil.sub(item.amount, item.amountRemaining)));

        helper.setText(R.id.tv_avg_price_title, String.format("成交均价(%s)", item.counterCurrency));
        helper.setText(R.id.tv_avg_price, BigDecimalUtil.stripTrailingZeros(item.priceAverage));

        helper.setText(R.id.tv_deal_quantity_title, String.format("成交量(%s)", item.currency));
        helper.setText(R.id.tv_deal_quantity, BigDecimalUtil.stripTrailingZeros(
                BigDecimalUtil.sub(item.quantity, item.quantityRemaining)));

        if (StringUtils.equals("0", item.status)) {
            if (StringUtils.equals("3", item.state)) {
                helper.setText(R.id.tv_status, "已完成");
/*            } else if (StringUtils.equals(item.state, "2")) {
                helper.setText(R.id.tv_status, "部分成交");*/
            } else {
                helper.setText(R.id.tv_status, "正常");
            }
        } else if (StringUtils.equals("1", item.status)) {
            helper.setText(R.id.tv_status, "已取消");
        } else if (StringUtils.equals("2", item.status)) {
            helper.setText(R.id.tv_status, "处理中");
        } else if (StringUtils.equals("4", item.status)) {
            helper.setText(R.id.tv_status, "待确认");
        }
    }
}
