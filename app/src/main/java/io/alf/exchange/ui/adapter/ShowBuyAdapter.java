package io.alf.exchange.ui.adapter;

import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import io.alf.exchange.R;
import io.alf.exchange.widget.OrderTextView;
import io.cex.mqtt.bean.OrderMarketPrice;


public class ShowBuyAdapter extends BaseQuickAdapter<OrderMarketPrice, BaseViewHolder> {

    private String totalNum;

    public ShowBuyAdapter(@Nullable List<OrderMarketPrice> data) {
        super(R.layout.item_show_buy_sell, data);
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
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
            orderTextView.bindPriceView(price, 0).setData(quantity, sum, totalNum, 0, progressBar);
        } else {
            orderTextView.bindPriceView("", 0).settvNumberNull(0, progressBar);
        }
        orderTextView.setItemPadding(2, 13).setTouchBackGround();
    }
}
