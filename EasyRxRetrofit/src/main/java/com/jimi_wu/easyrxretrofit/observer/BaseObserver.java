package com.jimi_wu.easyrxretrofit.observer;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by wuzhiming on 2019/2/21
 */
public abstract class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        _onError(e);
    }

    @Override
    public void onComplete() {
    }

    protected abstract void _onNext(T t);
    protected abstract void _onError(Throwable e);

}
