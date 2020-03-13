package io.tick.base.mvp;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.tick.base.net.HttpUtils;

public abstract class Presenter<V extends IView> implements IPresenter<V> {
    // 弱引用, 防止内存泄漏
    private WeakReference<V> mViewRef;
    // view代理，避免直接调用view造成空指针异常
    private V mViewProxy;

    public void attach(V view) {
        mViewRef = new WeakReference<>(view);
        ViewHandler viewHandler = new ViewHandler(mViewRef.get());
        mViewProxy = (V) Proxy.newProxyInstance(view.getClass().getClassLoader(),
                view.getClass().getInterfaces(), viewHandler);
    }

    private boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    @Override
    public V getView() {
        return mViewProxy;
    }

    @Override
    public void detach() {
        if (isViewAttached()) {
            //消所有子线程里面的网络连接。
            HttpUtils.cancelTag(getView());
            getView().hideLoading();
            mViewRef.clear();
            mViewRef = null;
        }
    }

    private class ViewHandler implements InvocationHandler {
        private IView view;

        public ViewHandler(IView view) {
            this.view = view;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (isViewAttached()) {
                return method.invoke(view, args);
            }
            return null;
        }
    }
}
