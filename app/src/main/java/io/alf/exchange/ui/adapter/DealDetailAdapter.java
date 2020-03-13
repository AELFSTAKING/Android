package io.alf.exchange.ui.adapter;


import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.bean.DealDetailBean;
import io.alf.exchange.util.SpannableUtils;
import io.alf.exchange.util.StringUtils;
import io.tick.base.util.DateUtil;


public class DealDetailAdapter extends BaseQuickAdapter<DealDetailBean, BaseViewHolder> {

    public DealDetailAdapter() {
        super(R.layout.item_deal_detail);
    }

    @Override
    protected void convert(BaseViewHolder helper, DealDetailBean item) {
        helper.setText(R.id.tv_time, DateUtil.formatDateTime(DateUtil.FMT_HH_MM_MM_DD, item.date));
        helper.setText(R.id.tv_price, item.price);
        helper.setText(R.id.tv_quantity, item.quantity);
        TextView view = helper.getView(R.id.tv_txid);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        String txid = StringUtils.ellipsize(item.txId, 30, 15);
        SpannableUtils.setHtmlTextView(view, txid, item.url);
        helper.addOnLongClickListener(R.id.tv_txid);
    }
}
