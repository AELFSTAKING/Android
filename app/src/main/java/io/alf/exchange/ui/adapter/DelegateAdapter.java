package io.alf.exchange.ui.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.bean.OrderAction;
import io.alf.exchange.bean.OrderType;
import io.alf.exchange.mvp.bean.OrderHistoryBean;
import io.alf.exchange.util.SpannableUtils;
import io.alf.exchange.widget.TradeCircleView;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DateUtil;


public class DelegateAdapter extends BaseQuickAdapter<OrderHistoryBean.ListBean, BaseViewHolder> {

    public DelegateAdapter() {
        super(R.layout.item_delegate);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderHistoryBean.ListBean item) {
        SpannableUtils.setShowTextView(helper.getView(R.id.tv_symbol_title), item.symbol, mContext,
                13, 13);
        TradeCircleView circle_view = helper.getView(R.id.circle_view);
        StringBuffer buffer = new StringBuffer();
        if (item.orderType.equals(OrderType.LMT)) {
            buffer.append(mContext.getString(R.string.limit_price));
        } else if (item.orderType.equals(OrderType.MKT)) {
            buffer.append(mContext.getString(R.string.cur_best_price));
        }
        switch (item.action) {
            case OrderAction.BUY:
                circle_view.setSecondColor(R.color.color_increase, true);
                helper.setTextColor(R.id.tv_buy_sell,
                        mContext.getResources().getColor(R.color.color_increase));
                helper.setText(R.id.tv_buy_sell,
                        buffer.append(mContext.getString(R.string.buy)).toString());
                break;
            case OrderAction.SELL:
                circle_view.setSecondColor(R.color.color_decrease, false);
                //circle_view.setProgress();
                helper.setTextColor(R.id.tv_buy_sell,
                        mContext.getResources().getColor(R.color.color_decrease));
                helper.setText(R.id.tv_buy_sell,
                        buffer.append(mContext.getString(R.string.sell)).toString());
                break;
        }
        helper.setText(R.id.tv_price, BigDecimalUtil.stripTrailingZeros(item.priceLimit));
        helper.setText(R.id.tv_date,
                DateUtil.formatDateTime(DateUtil.FMT_STD_DATE_TIME, item.utcCreate));
        if (item.orderType.equals(OrderType.LMT)) {
            helper.setText(R.id.tv_sl, "数量：");
            SpannableUtils.setShowTextView(helper.getView(R.id.tv_number),
                    BigDecimalUtil.stripTrailingZeros(
                            BigDecimalUtil.sub(item.quantity, item.quantityRemaining))
                            + "/" + BigDecimalUtil.stripTrailingZeros(item.quantity),
                    mContext, 13, 13);
            try {
                String div = BigDecimalUtil.divTrade(
                        BigDecimalUtil.stripTrailingZeros(
                                BigDecimalUtil.sub(item.quantity, item.quantityRemaining)),
                        BigDecimalUtil.stripTrailingZeros(item.quantity),
                        4);
                if (!TextUtils.isEmpty(div)) {
                    circle_view.setProgress(div);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (item.orderType.equals(OrderType.MKT)) {
            helper.setText(R.id.tv_sl, "交易额：");
            switch (item.action) {
                case OrderAction.BUY:
                    SpannableUtils.setShowTextView(helper.getView(R.id.tv_number),
                            BigDecimalUtil.stripTrailingZeros(
                                    BigDecimalUtil.sub(item.amount, item.amountRemaining)) + "/"
                                    + BigDecimalUtil.stripTrailingZeros(item.amount),
                            mContext, 13, 13);
                    try {
                        String div = BigDecimalUtil.divTrade(
                                BigDecimalUtil.stripTrailingZeros(
                                        BigDecimalUtil.sub(item.amount, item.amountRemaining)),
                                BigDecimalUtil.stripTrailingZeros(item.amount), 2);
                        if (!TextUtils.isEmpty(div)) {
                            circle_view.setProgress(div);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                case OrderAction.SELL:
                    SpannableUtils.setShowTextView(helper.getView(R.id.tv_number),
                            BigDecimalUtil.stripTrailingZeros(
                                    BigDecimalUtil.sub(item.quantity, item.quantityRemaining)) + "/"
                                    + BigDecimalUtil.stripTrailingZeros(item.quantity), mContext,
                            13, 13);
                    try {
                        String div = BigDecimalUtil.divTrade(
                                BigDecimalUtil.sub(item.quantity, item.quantityRemaining),
                                item.quantity, 2);
                        if (!TextUtils.isEmpty(div)) {
                            circle_view.setProgress(div);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        helper.addOnClickListener(R.id.tv_cancel);
/*        helper.setOnClickListener(R.id.cl_root, v -> ActivityStartUtils.jump(getContext(),
                TradeOrderDetailActivity.class,
                intent -> intent.putExtra(Constant.INTENT_OBJECT_DELEGATE_ITEM, item)));*/
    }
}
