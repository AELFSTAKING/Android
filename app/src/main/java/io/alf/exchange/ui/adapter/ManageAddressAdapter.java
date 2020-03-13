package io.alf.exchange.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import io.alf.exchange.R;
import io.alf.exchange.mvp.bean.BindAddressListBean;
import io.tick.base.util.DateUtil;


public class ManageAddressAdapter extends
        BaseQuickAdapter<BindAddressListBean.AddressListBean, BaseViewHolder> {

    public ManageAddressAdapter() {
        super(R.layout.item_manage_address);
    }

    @Override
    protected void convert(BaseViewHolder helper, BindAddressListBean.AddressListBean item) {
        helper.setText(R.id.tv_note_name, item.name);
        helper.setText(R.id.tv_time,
                DateUtil.formatDateTime(DateUtil.FMT_STD_DATE, item.gmtCreate));
        helper.setText(R.id.tv_address, item.address);
        helper.addOnClickListener(R.id.iv_delete);
    }
}
