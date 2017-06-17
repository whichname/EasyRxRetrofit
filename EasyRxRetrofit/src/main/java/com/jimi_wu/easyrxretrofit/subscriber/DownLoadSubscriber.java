package com.jimi_wu.easyrxretrofit.subscriber;

import android.content.Context;


import com.jimi_wu.easyrxretrofit.exception.ServerException;
import com.jimi_wu.easyrxretrofit.utils.NetworkUtils;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Created by wzm on 2017/6/17.
 */

public abstract class DownLoadSubscriber implements Subscriber<Object> {

    protected Context mContext;

    public DownLoadSubscriber(Context context) {
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
    public void onNext(Object o) {
        if (o instanceof Integer) {
            _onProgress((Integer) o);
        }

        if(o instanceof String) {
            _onNext((String) o);
        }
        mSubscription.request(1);
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

    protected abstract void _onNext(String result);

    protected abstract void _onProgress(Integer percent);

    protected abstract void _onError(int errorCode,String msg);

}
