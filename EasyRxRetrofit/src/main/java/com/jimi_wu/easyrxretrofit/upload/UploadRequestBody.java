package com.jimi_wu.easyrxretrofit.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by wzm on 2017/6/11.
 */
public class UploadRequestBody extends RequestBody {

    private File mFile;
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    public UploadRequestBody(File mFile) {
        this.mFile = mFile;
    }

    private UploadOnSubscribe mUploadOnSubscribe;

    public void setUploadOnSubscribe(UploadOnSubscribe uploadOnSubscribe) {
        this.mUploadOnSubscribe = uploadOnSubscribe;
    }

    @Override
    public MediaType contentType() {
        return MediaType.parse("multipart/form-data");
    }

    @Override
    public long contentLength() throws IOException {
        return mFile.length();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        FileInputStream in = new FileInputStream(mFile);

        try {
            int read;

            while ((read = in.read(buffer)) != -1) {

                // update progress on UI thread
                if(mUploadOnSubscribe != null) {
                    mUploadOnSubscribe.onRead(read);
                }

                sink.write(buffer, 0, read);
            }

        } finally {
            in.close();
        }

    }

}
