package io.alf.exchange.mvp.view;


import java.util.List;

import io.cex.mqtt.bean.QuotationBean;
import io.tick.base.mvp.IView;

public interface QuotationsView extends IView {
    void onQuerySymbolQuotation(List<QuotationBean> quotationList);
}
