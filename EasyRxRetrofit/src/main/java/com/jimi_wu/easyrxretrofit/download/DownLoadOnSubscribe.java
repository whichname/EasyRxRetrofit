package com.jimi_wu.easyrxretrofit.download;

import java.io.File;
import java.io.IOException;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by wzm on 2017/6/15.
 */

public class DownLoadOnSubscribe implements FlowableOnSubscribe<Object> {

    private FlowableEmitter<Object> mFlowableEmitter;

    //    默认保存地址
    private String mPath;
    //    文件名
    private String mFileName;

    //    已上传
    private long mUploaded = 0l;
    //    总长度
    private long mSumLength = 0l;
    private int mPercent = 0;

    private Source mSource;
    private Source mProgressSource;
    private BufferedSink mSink;

    public DownLoadOnSubscribe(ResponseBody responseBody, String path, String fileName) throws IOException {
        this.mPath = path;
        this.mFileName = fileName;
        init(responseBody);
    }

    @Override
    public void subscribe(@NonNull FlowableEmitter<Object> e) {
        this.mFlowableEmitter = e;
        try {
            mSink.writeAll(Okio.buffer(mProgressSource));
            mSink.close();
            mFlowableEmitter.onNext(mPath+mFileName);
            mFlowableEmitter.onComplete();
        } catch (Exception exception) {
            mFlowableEmitter.onError(exception);
        }
    }


    private void init(ResponseBody responseBody) throws IOException {
        mSumLength = responseBody.contentLength();
        mSource = responseBody.source();

        mProgressSource = getProgressSource(mSource);

        mSink = Okio.buffer(Okio.sink(new File(mPath + mFileName)));

    }

    public void onRead(long read) {
        mUploaded += read == -1 ? 0 : read;
        onProgress((int) (100 * mUploaded / mSumLength));
    }

    private void onProgress(int percent) {
        if (mFlowableEmitter == null) return;
        if (percent == mPercent) return;
        mPercent = percent;
        if (percent >= 100) {
            percent = 100;
            mFlowableEmitter.onNext(percent);
            return;
        }
        mFlowableEmitter.onNext(percent);
    }

    private ForwardingSource getProgressSource(Source source) {
        return new ForwardingSource(source) {
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long read = super.read(sink, byteCount);
                onRead(read);
                return read;
            }
        };
    }


}
