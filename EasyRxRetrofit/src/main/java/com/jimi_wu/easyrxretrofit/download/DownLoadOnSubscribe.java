package com.jimi_wu.easyrxretrofit.download;

import android.util.Pair;

import java.io.File;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

public class DownLoadOnSubscribe implements ObservableOnSubscribe<Object> {

    private ObservableEmitter<Object> mObservableEmitter;

    //    默认保存地址
    private String mPath;
    //    文件名
    private String mFileName;
    //    临时文件名
    private String mFileNameTmp;

    //    已下载
    private long mDownLoaded = 0l;
    //    总长度
    private long mSumLength = 0l;

    private Source mSource;
    private Source mProgressSource;
    private BufferedSink mSink;

    public DownLoadOnSubscribe(ResponseBody responseBody, String path, String fileName) throws IOException {
        this.mPath = path;
        this.mFileName = fileName;
        this.mFileNameTmp = fileName + ".tmp";
        createFile();
        init(responseBody);
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Object> e) {
        this.mObservableEmitter = e;
        try {
            mSink.writeAll(Okio.buffer(mProgressSource));
            mSink.close();
            File file = new File(mPath +File.separator + mFileNameTmp);
            File destFile = new File(mPath +File.separator + mFileName);
            if (destFile.exists()) {
                destFile.delete();
            }
            file.renameTo(new File(mPath +File.separator + mFileName));
            mObservableEmitter.onNext(mPath+File.separator+mFileName);
            mObservableEmitter.onComplete();
        } catch (Exception exception) {
            mObservableEmitter.onError(exception);
        }
    }


    private void init(ResponseBody responseBody) throws IOException {
        mSumLength = responseBody.contentLength();

        mSource = responseBody.source();

        mProgressSource = getProgressSource(mSource);

        mSink = Okio.buffer(Okio.sink(new File(mPath +File.separator + mFileNameTmp)));

    }

    public void onRead(long read) {
        mDownLoaded += read == -1 ? 0 : read;
        if (mObservableEmitter == null) return;
        if (mDownLoaded >= mSumLength) {
            mDownLoaded = mSumLength;
        }
        mObservableEmitter.onNext(Pair.create(mDownLoaded, mSumLength));
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

    private void createFile() throws IOException {
        File path = new File(mPath);
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(mPath + File.separator + mFileNameTmp);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

}
