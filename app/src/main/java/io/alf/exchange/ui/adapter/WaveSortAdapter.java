package io.alf.exchange.ui.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.util.PriceConvertUtil;
import io.alf.exchange.util.SpannableUtils;
import io.cex.mqtt.bean.QuotationBean;


public class WaveSortAdapter extends BaseQuickAdapter<QuotationBean, BaseViewHolder> {

    public WaveSortAdapter() {
        super(R.layout.item_sort_quotation_wave);
    }

    @Override
    protected void convert(BaseViewHolder helper, QuotationBean item) {
        SpannableUtils.setShowTextView(helper.getView(R.id.tv_symbol), item.symbol, mContext, 16,
                13);
        helper.setText(R.id.tv_price, item.lastPrice);
        helper.setText(R.id.tv_quantity, "24H量" + item.quantity);
        helper.setText(R.id.tv_legal_price,
                "≈ " + PriceConvertUtil.getUsdtAmount(item.lastUsdPrice));
        helper.setText(R.id.tv_wave_percent,
                PriceConvertUtil.getWavePercent(item.direction, item.wavePercent));
        if (TextUtils.equals(item.direction, "-1")) {
            helper.setBackgroundRes(R.id.tv_wave_percent, R.drawable.bg_quotation_decrease);
        } else if (TextUtils.equals(item.direction, "0")) {
            helper.setBackgroundRes(R.id.tv_wave_percent, R.drawable.bg_quotation_normal);
        } else if (TextUtils.equals(item.direction, "1")) {
            helper.setBackgroundRes(R.id.tv_wave_percent, R.drawable.bg_quotation_increase);
        }
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_symbol_title));
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_price));
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_wave_percent));
    }
}
