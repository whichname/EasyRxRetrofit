package com.jimi_wu.easyrxretrofit.download;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by wzm on 2017/6/16.
 */

public class DownLoadTransformer implements ObservableTransformer<ResponseBody, Object> {

    //    默认保存地址
    private String mPath;
    //    文件名
    private String mFileName;

    public DownLoadTransformer(String mPath, String mFileName) {
        this.mPath = mPath;
        this.mFileName = mFileName;
    }

    @Override
    public ObservableSource<Object> apply(Observable<ResponseBody> upstream) {
        return upstream.flatMap(new Function<ResponseBody, ObservableSource<?>>() {
            @Override
            public ObservableSource<Object> apply(ResponseBody responseBody) throws Exception {
                return Observable.create(new DownLoadOnSubscribe(responseBody, mPath, mFileName));
            }
        });
    }
}
