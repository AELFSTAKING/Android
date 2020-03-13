package io.alf.exchange.ui.adapter;

import android.text.TextUtils;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import io.alf.exchange.R;
import io.alf.exchange.widget.OrderTextView;
import io.cex.mqtt.bean.OrderMarketPrice;


/**
 * 功能描述：  显示卖的数据的adapter
 */
public class ShowSellAdapter extends BaseQuickAdapter<OrderMarketPrice, BaseViewHolder> {

    private String totalNum;
    private String initColor;

    public ShowSellAdapter(@Nullable List<OrderMarketPrice> data) {
        super(R.layout.item_show_buy_sell, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderMarketPrice item) {
        helper.addOnClickListener(R.id.tv_select_order);
        OrderTextView orderTextView = (OrderTextView) helper.getView(R.id.tv_select_order);
        ProgressBar progressBar = orderTextView.findViewById(R.id.progressBar);
        if (item != null) {
            String price = item.limitPrice;
            String quantity = item.quantity;
            String sum = item.sum + "";
            orderTextView.bindPriceView(price, 1).setData(quantity, sum, totalNum, 1, progressBar);
        } else {
            if (!TextUtils.isEmpty(initColor)) {
                orderTextView.bindPriceView("", 1).settvNumberNull(2, progressBar);
            } else {
                orderTextView.bindPriceView("", 1).settvNumberNull(1, progressBar);
            }
        }
        orderTextView.setItemPadding(2, 13).setTouchBackGround();
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public void initColor(String initColor) {
        this.initColor = initColor;
    }
}
