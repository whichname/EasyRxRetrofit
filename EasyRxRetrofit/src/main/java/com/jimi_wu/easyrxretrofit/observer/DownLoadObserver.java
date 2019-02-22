package com.jimi_wu.easyrxretrofit.observer;


import android.util.Pair;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by wuzhiming on 2019/2/21
 */
public abstract class DownLoadObserver implements Observer<Object> {

    private int mPercent = 0;

    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(Object o) {
        if (o instanceof Pair) {
            long uploaded = (long)(((Pair) o).first);
            long sumLength = (long)(((Pair) o).second);
            _onProgress(uploaded, sumLength);
            int percent = (int) (uploaded*100f / sumLength);
            if (percent < 0) {
                percent = 0;
            }
            if (percent > 100) {
                percent = 100;
            }
            if (percent == mPercent) {
                return;
            }
            mPercent = percent;
            _onProgress(mPercent);
        }
        if(o instanceof String) {
            _onNext((String) o);
        }
    }

    @Override
    public void onError(Throwable e) {
        _onError(e);
    }

    @Override
    public void onComplete() {
    }

    public abstract void _onNext(String result);
    public void _onProgress(Integer percent) {}
    public abstract void _onError(Throwable e);
    public void _onProgress(long uploaded, long sumLength) {}

}
