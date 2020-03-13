package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.CurrencyBean;
import io.alf.exchange.util.CexDataPersistenceUtils;
import io.alf.exchange.util.PriceConvertUtil;
import io.tick.base.util.BigDecimalUtil;


public class AssetAdapter extends BaseQuickAdapter<CurrencyBean.ListBean, BaseViewHolder> {

    public AssetAdapter() {
        super(R.layout.item_asset);
    }

    @Override
    protected void convert(BaseViewHolder helper, CurrencyBean.ListBean item) {
        ((SimpleDraweeView) helper.getView(R.id.iv_icon)).setImageURI(item.icon128);
        helper.setText(R.id.tv_currency, item.alias);
        helper.setText(R.id.tv_chain, item.tokenChain);
        String balance = CexDataPersistenceUtils.getBalance(item.tokenChain, item.tokenCurrency);
        helper.setText(R.id.tv_amount, CexDataPersistenceUtils.isHideAssets() ? "***"
                : BigDecimalUtil.formatByDecimalValue(balance));

        String usdtBalance = "≈ " + PriceConvertUtil.getUsdtAmount(item.tokenCurrency, balance);
        helper.setText(R.id.tv_usdt_amount,
                CexDataPersistenceUtils.isHideAssets() ? "***" : usdtBalance);
/*        if (!TextUtils.isEmpty(item.balance)) {
            helper.setText(R.id.tv_amount, item.balance);
        }*/
        //helper.setText(R.id.tv_amount, item.balance);
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_symbol_title));
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_price));
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_wave_percent));
    }
}
