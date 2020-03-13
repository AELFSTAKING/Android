package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.BindAddressListBean;


public class BindAddressAdapter extends
        BaseQuickAdapter<BindAddressListBean.AddressListBean, BaseViewHolder> {

    public BindAddressAdapter() {
        super(R.layout.item_bind_address);
    }

    @Override
    protected void convert(BaseViewHolder helper, BindAddressListBean.AddressListBean item) {
        helper.setText(R.id.tv_note_name, item.name);
        helper.setText(R.id.tv_address, item.address);
        //helper.setText(R.id.tv_amount, item.balance);
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_symbol_title));
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_price));
        //FontUtil.setDinMediumFont(helper.getView(R.id.tv_wave_percent));
    }
}
