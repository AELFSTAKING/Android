package io.tick.base.mvp;

import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MvpActivity extends BaseActivity {

    public final String TAG = this.getClass().getSimpleName();
    private Map<String, IPresenter> mPresenterMap = new ConcurrentHashMap<>();

    protected abstract void initPresenter();

    protected abstract void initData();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        initData();
    }

    @Override
    protected void onDestroy() {
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
