package com.jimi_wu.easyrxretrofit.download;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by wzm on 2017/6/15.
 */

public interface DownLoadService {

    @Streaming
    @GET
    Observable<ResponseBody> startDownLoad(@Url String fileUrl);

}
