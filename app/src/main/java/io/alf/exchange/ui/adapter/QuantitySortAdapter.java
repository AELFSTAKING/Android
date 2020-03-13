package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.util.PriceConvertUtil;
import io.alf.exchange.util.SpannableUtils;
import io.cex.mqtt.bean.QuotationBean;


public class QuantitySortAdapter extends BaseQuickAdapter<QuotationBean, BaseViewHolder> {

    public QuantitySortAdapter() {
        super(R.layout.item_sort_quotation_quantity);
    }

    @Override
    protected void convert(BaseViewHolder helper, QuotationBean item) {
        SpannableUtils.setShowTextView(helper.getView(R.id.tv_symbol), item.symbol, mContext, 16,
                13);
        helper.setText(R.id.tv_price, item.lastPrice);
        helper.setText(R.id.tv_quantity, item.quantity);
        helper.setText(R.id.tv_wave_percent,
                PriceConvertUtil.getWavePercent(item.direction, item.wavePercent));
        helper.setText(R.id.tv_legal_price,
                "≈ " + PriceConvertUtil.getUsdtAmount(item.lastUsdPrice));
    }
}
