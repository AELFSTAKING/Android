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


public class DelegateAdapter2 extends BaseQuickAdapter<OrderHistoryBean.ListBean, BaseViewHolder> {

    public DelegateAdapter2() {
        super(R.layout.item_delegate_2);
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
                helper.setText(R.id.tv_freeze_title, String.format("冻结(%s)", item.freezeCurrency));
                helper.setText(R.id.tv_freeze,
                        BigDecimalUtil.stripTrailingZeros(item.freezeAmount));
                break;
            case OrderAction.SELL:
                helper.setTextColor(R.id.tv_buy_sell,
                        mContext.getResources().getColor(R.color.color_decrease));
                helper.setText(R.id.tv_buy_sell,
                        buffer.append(mContext.getString(R.string.sell)).toString());
                helper.setBackgroundRes(R.id.tv_buy_sell, R.drawable.bg_decrease_stroke);
                helper.setText(R.id.tv_freeze_title, String.format("冻结(%s)", item.freezeCurrency));
                helper.setText(R.id.tv_freeze,
                        BigDecimalUtil.stripTrailingZeros(item.freezeAmount));
                break;
        }
        helper.setText(R.id.tv_symbol_title, item.symbol);
        helper.setText(R.id.tv_date,
                DateUtil.formatDateTime(DateUtil.FMT_HH_MM_MM_DD, item.utcCreate));

        helper.setText(R.id.tv_price_title, String.format("委托价(%s)", item.counterCurrency));
        helper.setText(R.id.tv_price, BigDecimalUtil.stripTrailingZeros(item.priceLimit));

        helper.setText(R.id.tv_quantity_title, String.format("委托量(%s)", item.currency));
        helper.setText(R.id.tv_quantity, BigDecimalUtil.stripTrailingZeros(item.quantity));

        if (StringUtils.equals(item.status, "4")) {
            helper.setGone(R.id.tv_cancel, false);
            helper.setGone(R.id.tv_status, true);
            helper.setText(R.id.tv_status, "待确认");
        } else if (StringUtils.equals(item.status, "0")) {
            helper.setGone(R.id.tv_cancel, true);
            helper.setGone(R.id.tv_status, false);
        }

        helper.addOnClickListener(R.id.tv_cancel);
    }
}
