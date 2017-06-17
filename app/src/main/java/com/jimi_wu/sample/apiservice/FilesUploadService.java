package com.jimi_wu.sample.apiservice;

import com.jimi_wu.sample.Constants;
import com.jimi_wu.sample.model.FileBean;
import com.jimi_wu.sample.model.ResultBean;

import java.util.ArrayList;

import io.reactivex.Flowable;
import okhttp3.MultipartBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by wuzhiming on 2016/11/17.
 */

public interface FilesUploadService {

    @Multipart
    @POST(Constants.UPLOADS_URL)
    Flowable<ResultBean<ArrayList<FileBean>>> uploads(@Part ArrayList<MultipartBody.Part> files);

}
