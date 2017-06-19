package com.jimi_wu.sample.apiservice;

import com.jimi_wu.sample.Constants;
import com.jimi_wu.sample.model.FileBean;
import com.jimi_wu.sample.model.ResultBean;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by wuzhiming on 2016/11/14.
 */

public interface FileUploadService {

    @Multipart
    @POST(Constants.UPLOAD_URL)
    Flowable<ResultBean<FileBean>> upload(@Part MultipartBody.Part file);

}
