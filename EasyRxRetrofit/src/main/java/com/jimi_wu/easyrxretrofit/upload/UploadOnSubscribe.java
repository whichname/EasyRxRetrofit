package com.jimi_wu.easyrxretrofit.upload;


import android.util.Pair;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by wzm on 2017/6/11.
 */

public class UploadOnSubscribe implements ObservableOnSubscribe<Object> {

    private ObservableEmitter<Object> mObservableEmitter;
    private long mSumLength = 0L;
    private long mUploaded = 0L;

    public UploadOnSubscribe() {
    }

    public void setSumLength(long sumLength) {
        this.mSumLength = sumLength;
    }

    public long getSumLength() {
        return mSumLength;
    }

    public void addSumLength(long length) {
        this.mSumLength += length;
    }

    public void onRead(long read) {
        mUploaded += read;
        if (mObservableEmitter == null) return;
        if (mUploaded >= mSumLength) {
            mUploaded = mSumLength;
        }
        mObservableEmitter.onNext(Pair.create(mUploaded, mSumLength));
    }

    @Override
    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
        mObservableEmitter = emitter;
    }


}
