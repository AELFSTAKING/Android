package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.QuotationHistoryBean;
import io.tick.base.mvp.IView;

public interface QuotationKLineDetailView extends IView {
    void onQueryQuotationHistory(QuotationHistoryBean historyBean);

    void onQueryQuotationHistoryError();

    void onChangeRefreshStatus(boolean b);
}
