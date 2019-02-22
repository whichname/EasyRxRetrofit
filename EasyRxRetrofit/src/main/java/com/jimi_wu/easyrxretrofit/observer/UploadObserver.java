package com.jimi_wu.easyrxretrofit.observer;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * created by wuzhiming on 2019/2/21
 */
public abstract class UploadObserver<T> implements Observer<Object> {

    private int mPercent = 0;
    private Type mType;

    public UploadObserver() {
        mType = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

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
            return;
        }
        if (o instanceof JsonObject) {
            Gson gson = new Gson();
            try {
                T result = gson.fromJson((JsonElement) o, mType);
                _onNext(result);
            } catch (Exception e) {
                e.printStackTrace();
                _onError(e);
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        _onError(e);
    }

    @Override
    public void onComplete() {
    }

    public abstract void _onNext(T t);

    public void _onProgress(Integer percent) {}

    public void _onProgress(long uploaded, long sumLength) {}

    public abstract void _onError(Throwable e);

}
