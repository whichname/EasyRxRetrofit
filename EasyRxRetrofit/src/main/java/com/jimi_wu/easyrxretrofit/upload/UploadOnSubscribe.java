package com.jimi_wu.easyrxretrofit.upload;


import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;

/**
 * Created by wzm on 2017/6/11.
 */

public class UploadOnSubscribe implements FlowableOnSubscribe<Integer> {

    private FlowableEmitter<Integer> mObservableEmitter;
    private long sumLength = 0l;
    private long uploaded = 0l;

    private int mPercent = 0;

    public UploadOnSubscribe(long sumLength) {
        this.sumLength = sumLength;
    }

    public void onRead(long read) {
        uploaded+=read;
        onProgress((int) (100*uploaded/sumLength));
    }

    private void onProgress(int percent) {
        if (mObservableEmitter == null) return;
        if(percent == mPercent) return;
        mPercent = percent;
        if (percent >= 100) {
            percent = 100;
            mObservableEmitter.onNext(percent);
            mObservableEmitter.onComplete();
            return;
        }
        mObservableEmitter.onNext(percent);
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<Integer> e) throws Exception {
        this.mObservableEmitter = e;
    }

}
