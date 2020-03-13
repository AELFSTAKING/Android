package io.alf.exchange.mvp.presenter;


import com.lzy.okgo.model.Response;

import io.alf.exchange.mvp.bean.AllArticleList;
import io.alf.exchange.mvp.view.QueryAllArticleView;
import io.tick.base.net.BaseCallback;
import io.tick.base.net.BaseUrl;
import io.tick.base.net.HttpUtils;
import io.tick.base.net.response.ResponseBean;

public class QueryAllArticlePresenter extends BasePresenter<QueryAllArticleView> {

    /**
     * 获取web首页banner、滚动公告、资讯
     */
    public void queryAllArticleList(boolean showLoading) {
        HttpUtils.postRequest(BaseUrl.GET_ALL_ARTICLE_LIST, getView(), generateRequestHeader(), generateRequestBody(),
                new BaseCallback<ResponseBean<AllArticleList>>(BaseUrl.GET_ALL_ARTICLE_LIST, getView(), showLoading) {
                    @Override
                    public void onSuccess(Response<ResponseBean<AllArticleList>> response) {
                        super.onSuccess(response);
                        if (response.body() != null) {
                            response.body().showErrorMsg();
                        }
                        if (response.body().isSuccess()) {
                            getView().onQueryAllArticleList(response.body().data);
                        }
                    }
                });
    }
}
