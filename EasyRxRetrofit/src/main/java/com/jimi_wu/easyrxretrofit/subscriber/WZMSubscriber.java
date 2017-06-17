package com.jimi_wu.easyrxretrofit.subscriber;

import android.content.Context;


import com.jimi_wu.easyrxretrofit.exception.ServerException;
import com.jimi_wu.easyrxretrofit.utils.NetworkUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Created by wuzhiming on 2016/11/14.
 */

public abstract class WZMSubscriber<T> implements Subscriber<T> {
    protected Context mContext;

    public WZMSubscriber(Context context) {
        this.mContext = context;
    }

    protected Subscription mSubscription;

    @Override
    public void onSubscribe(Subscription s) {
        this.mSubscription = s;
        mSubscription.request(1);
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        if (!NetworkUtils.isNetworkAvailable(mContext)) {
            _onError(ServerException.ERROR_NETWORK,"网络不可用");
        } else if (e instanceof ServerException) {
            _onError(((ServerException) e).getErrorCode(),e.getMessage());
        } else {
            _onError(ServerException.ERROR_OTHER,e.getMessage());
        }
    }

    @Override
    public void onNext(T t) {
        _onNext(t);
        mSubscription.request(1);
    }


    protected abstract void _onNext(T t);
    protected abstract void _onError(int errorCode,String msg);

}
