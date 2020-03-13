package io.tick.base.mvp

interface IPresenter<V : IView> {
    val view: V
    fun detach()
}
