package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.TotalAssetBean;
import io.tick.base.mvp.IView;

public interface QueryAssetsView extends IView {
    void onQueryAssetList(TotalAssetBean data);
}
