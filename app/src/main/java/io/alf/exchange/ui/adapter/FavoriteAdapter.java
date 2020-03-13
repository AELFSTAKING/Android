package io.alf.exchange.ui.adapter;

import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.Favorites;
import io.alf.exchange.util.PriceConvertUtil;
import io.alf.exchange.util.SpannableUtils;
import io.cex.mqtt.bean.QuotationBean;


public class FavoriteAdapter extends BaseQuickAdapter<QuotationBean, BaseViewHolder> {

    private Favorites mFavorites;
    private boolean mShowFavorite;

    public FavoriteAdapter() {
        super(R.layout.item_favorite);
    }

    @Override
    protected void convert(BaseViewHolder helper, QuotationBean item) {
        SpannableUtils.setShowTextView(helper.getView(R.id.tv_symbol), item.symbol, mContext, 16,
                13);
        helper.setText(R.id.tv_price, item.lastPrice);
        helper.setText(R.id.tv_quantity, "24H量" + item.quantity);
        helper.setText(R.id.tv_legal_price,
                "≈ " + PriceConvertUtil.getUsdtAmount(item.lastUsdPrice));
        helper.setGone(R.id.tv_wave_percent, !mShowFavorite);
        helper.setGone(R.id.iv_favorite, mShowFavorite);
        if (mShowFavorite) {
            if (mFavorites != null && mFavorites.getSymbolList() != null
                    && mFavorites.getSymbolList().contains(item.symbol)) {
                helper.setImageResource(R.id.iv_favorite, R.mipmap.ic_favorite_selected);
            } else {
                helper.setImageResource(R.id.iv_favorite, R.mipmap.ic_favorite_normal);
            }
            helper.addOnClickListener(R.id.iv_favorite);
        } else {
            helper.setText(R.id.tv_wave_percent,
                    PriceConvertUtil.getWavePercent(item.direction, item.wavePercent));
            if (TextUtils.equals(item.direction, "-1")) {
                helper.setBackgroundRes(R.id.tv_wave_percent, R.drawable.bg_quotation_decrease);
            } else if (TextUtils.equals(item.direction, "0")) {
                helper.setBackgroundRes(R.id.tv_wave_percent, R.drawable.bg_quotation_normal);
            } else if (TextUtils.equals(item.direction, "1")) {
                helper.setBackgroundRes(R.id.tv_wave_percent, R.drawable.bg_quotation_increase);
            }
        }
    }

    public void setFavorites(Favorites data) {
        mFavorites = data;
    }

    public Favorites getFavorites() {
        return mFavorites;
    }

    public void setShowFavorite(boolean showFavorite) {
        mShowFavorite = showFavorite;
    }
}
