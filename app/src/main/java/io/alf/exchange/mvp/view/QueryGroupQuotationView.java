package io.alf.exchange.mvp.view;


import java.util.List;

import io.cex.mqtt.bean.QuotationGroupBean;
import io.tick.base.mvp.IView;

public interface QueryGroupQuotationView extends IView {
    void onQueryQuotationsGroupBy(int actionId, List<QuotationGroupBean> data);
}
