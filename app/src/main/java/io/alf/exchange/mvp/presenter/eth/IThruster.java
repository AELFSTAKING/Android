package io.alf.exchange.mvp.presenter.eth;

public interface IThruster {
    interface Callback<T> {
        void onSuccess(T tx);
    }
}
