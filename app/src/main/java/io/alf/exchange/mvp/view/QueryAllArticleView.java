package io.alf.exchange.mvp.view;


import io.alf.exchange.mvp.bean.AllArticleList;
import io.tick.base.mvp.IView;

public interface QueryAllArticleView extends IView {
    /**
     * banner 和 通知
     *
     * @param articleList
     */
    void onQueryAllArticleList(AllArticleList articleList);
}
