package io.alf.exchange.mvp.view;


import java.util.List;

import io.cex.mqtt.bean.QuotationGroupByAreaBean;
import io.tick.base.mvp.IView;

public interface QueryGroupQuotationByAreaView extends IView {
    void onQueryQuotationsGroupByArea(int actionId, List<QuotationGroupByAreaBean> data);
}
