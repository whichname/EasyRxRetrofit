package com.jimi_wu.sample.apiservice;

import com.jimi_wu.sample.model.FileBean;
import com.jimi_wu.sample.model.ResultBean;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by wzm on 2017/6/15.
 */

public interface FileDownLoadService {

    @GET
    Flowable<ResultBean<FileBean>> download(@Url String fileUrl);

}
