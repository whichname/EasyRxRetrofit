package com.jimi_wu.easyrxretrofit.download;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by wzm on 2017/6/16.
 */

public class DownLoadTransformer implements FlowableTransformer<ResponseBody, Object> {


//    默认保存地址
    private String mPath;
//    文件名
    private String mFileName;

    public DownLoadTransformer(String mPath, String mFileName) {
        this.mPath = mPath;
        this.mFileName = mFileName;
    }

    @Override
    public Publisher<Object> apply(@NonNull Flowable<ResponseBody> upstream) {
        return upstream.flatMap(new Function<ResponseBody, Publisher<Object>>() {
            @Override
            public Publisher<Object> apply(@NonNull ResponseBody responseBody) throws Exception {
                DownLoadOnSubscribe downLoadOnSubscribe = new DownLoadOnSubscribe(responseBody, mPath, mFileName);
                return Flowable.create(downLoadOnSubscribe, BackpressureStrategy.BUFFER);
            }
        });
    }

}
