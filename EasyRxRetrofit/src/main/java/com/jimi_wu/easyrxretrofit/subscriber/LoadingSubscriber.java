package com.jimi_wu.easyrxretrofit.subscriber;

import android.content.Context;

import org.reactivestreams.Subscription;

/**
 * Created by wuzhiming on 2016/11/14.
 */

public abstract class LoadingSubscriber<T> extends WZMSubscriber<T> {

    public LoadingSubscriber(Context mContext) {
        super(mContext);
    }

    @Override
    public void onSubscribe(Subscription s) {
        super.onSubscribe(s);
        showLoadingView();
    }

    @Override
    public void onComplete() {
        super.onComplete();
        hideLoadingView();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        hideLoadingView();
    }

    protected abstract void showLoadingView();
    protected abstract void hideLoadingView();

}
