package io.alf.exchange.ui.adapter;

import static io.tick.base.BaseApp.getContext;

import android.text.TextUtils;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.OrderHistoryBean;
import io.alf.exchange.util.SpannableUtils;
import io.tick.base.util.BigDecimalUtil;
import io.tick.base.util.DateUtil;


public class HistoryDelegateAdapter extends
        BaseQuickAdapter<OrderHistoryBean.ListBean, BaseViewHolder> {
    public HistoryDelegateAdapter() {
        super(R.layout.item_history_delegate_old);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderHistoryBean.ListBean item) {
        ImageView ivStatus = helper.getView(R.id.iv_status);
        switch (item.action) {
            case "2":
                ivStatus.setImageResource(R.mipmap.ic_history_buy);
                break;
            case "1":
                ivStatus.setImageResource(R.mipmap.ic_history_sell);
                break;
        }
        SpannableUtils.setShowTextView(helper.getView(R.id.tv_name), item.symbol, getContext(), 13,
                13);
        if (item.utcCreate != 0) {
            helper.setText(R.id.tv_date,
                    DateUtil.formatDateTime(DateUtil.FMT_MM_DD_HH_MM_SS,
                            item.utcCreate));
        }
        if (!TextUtils.isEmpty(item.priceAverage)) {
            helper.setText(R.id.tv_avg_price, BigDecimalUtil.stripTrailingZeros(item.priceAverage));
        }
        if (!TextUtils.isEmpty(item.priceLimit)) {
            helper.setText(R.id.tv_limit_price, BigDecimalUtil.stripTrailingZeros(item.priceLimit));
        }
        if (!TextUtils.isEmpty(item.quantity) && !TextUtils.isEmpty(item.quantityRemaining)) {
            helper.setText(R.id.tv_deal_quantity,
                    BigDecimalUtil.stripTrailingZeros(
                            BigDecimalUtil.sub(item.quantity, item.quantityRemaining)));
        }
        if (!TextUtils.isEmpty(item.quantity)) {
            helper.setText(R.id.tv_quantity, BigDecimalUtil.stripTrailingZeros(item.quantity));
        }
/*        helper.setOnClickListener(R.id.cl_root, v -> ActivityStartUtils.jump(getContext(),
                TradeOrderDetailActivity.class,
                intent -> intent.putExtra(Constant.INTENT_OBJECT_DELEGATE_ITEM, item)));*/
    }
}
