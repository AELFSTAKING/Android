package io.alf.exchange.ui.adapter;

import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.widget.OrderTextView;
import io.cex.mqtt.bean.SellInfo;

public class BuyEightAdapter extends BaseQuickAdapter<SellInfo, BaseViewHolder> {

    private String totalNum;

    public BuyEightAdapter() {
        super(R.layout.item_show_buy_sell);
    }

    @Override
    protected void convert(BaseViewHolder helper, SellInfo item) {
        OrderTextView orderTextView = helper.getView(R.id.tv_select_order);
        ProgressBar progressBar = helper.getView(R.id.progressBar);
        String price = item.price;
        String quantity = item.quantity;
        double sum = item.sum;
        orderTextView
                .bindPriceView(price, 0)
                .setData(quantity, sum + "", totalNum, 2, progressBar);
        orderTextView
                .setItemPadding(2, 8)
                .setTouchBackGround();
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }
}
