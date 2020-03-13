package io.tick.base.mvp;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MvpFragment extends BaseFragment {

    public final String TAG = this.getClass().getSimpleName();
    private Map<String, IPresenter> mPresenterMap = new ConcurrentHashMap<>();

    protected abstract void initPresenter();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initPresenter();
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        initPresenter();
    }

    @Override
    public void onDestroy() {
        detachPresenter();//释放资源
        super.onDestroy();
    }

    private void detachPresenter() {
        Set<String> keySet = mPresenterMap.keySet();
        for (String key : keySet) {
            mPresenterMap.remove(key).detach();
        }
    }

    public <V extends IView, P extends Presenter<V>> P registerPresenter(P presenter, V view) {
        if (presenter != null) {
            presenter.attach(view);
            mPresenterMap.put(presenter.getClass().getSimpleName(), presenter);
        }
        return presenter;
    }
}
