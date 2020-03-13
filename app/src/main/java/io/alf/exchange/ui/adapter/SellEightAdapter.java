package io.alf.exchange.ui.adapter;

import android.widget.ProgressBar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.widget.OrderTextView;
import io.cex.mqtt.bean.SellInfo;

public class SellEightAdapter extends BaseQuickAdapter<SellInfo, BaseViewHolder> {

    private String totalNum;

    public SellEightAdapter() {
        super(R.layout.item_show_buy_sell);
    }

    @Override
    protected void convert(BaseViewHolder helper, SellInfo item) {
        if (item != null) {
            OrderTextView orderTextView = helper.getView(R.id.tv_select_order);
            ProgressBar progressBar = orderTextView.findViewById(R.id.progressBar);

            String price = item.price;
            String quantity = item.quantity;
            String sum = item.sum + "";

            // type  0==买   1==卖
            orderTextView
                    .bindPriceView(price, 1)
                    .setData(quantity, sum, totalNum, 2, progressBar);
            orderTextView
                    .setItemPadding(2, 8)
                    .setTouchBackGround();
        }
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }
}
