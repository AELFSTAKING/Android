package io.alf.exchange.mvp.view;

import java.util.List;

import io.tick.base.mvp.IView;

public interface QueryDepthStepView extends IView {
    void onQueryDepthStep(List<String> data);
}
