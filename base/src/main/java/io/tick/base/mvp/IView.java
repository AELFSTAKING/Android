package io.tick.base.mvp;

public interface IView {

    /**
     * 显示loading框
     */
    void showLoading();

    /**
     * 隐藏loading框
     */
    void hideLoading();

    void toast(CharSequence s);
}

