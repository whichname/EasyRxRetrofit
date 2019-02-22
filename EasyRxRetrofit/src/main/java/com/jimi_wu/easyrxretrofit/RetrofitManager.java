package com.jimi_wu.easyrxretrofit;

import android.text.TextUtils;

import com.jimi_wu.easyrxretrofit.build.DefaultRetrofitBuilder;
import com.jimi_wu.easyrxretrofit.build.RetrofitBuilder;
import com.jimi_wu.easyrxretrofit.download.DownLoadService;
import com.jimi_wu.easyrxretrofit.download.DownLoadTransformer;
import com.jimi_wu.easyrxretrofit.transformer.BaseModel;
import com.jimi_wu.easyrxretrofit.transformer.BaseModelTransformer;
import com.jimi_wu.easyrxretrofit.upload.UploadOnSubscribe;
import com.jimi_wu.easyrxretrofit.upload.UploadParam;
import com.jimi_wu.easyrxretrofit.upload.UploadRequestBody;
import com.jimi_wu.easyrxretrofit.upload.UploadService;
import com.jimi_wu.easyrxretrofit.utils.FileUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

/**
 * Created by wzm on 2017/6/14.
 */
public class RetrofitManager {

    /**
     * 初始化
     */
    private static Retrofit mRetrofit;

    public static void init(RetrofitBuilder builder) {
        mRetrofit = builder.initRetrofit();
    }

    public static Retrofit getRetrofit() {
        if (mRetrofit == null) {
            mRetrofit = new DefaultRetrofitBuilder().initRetrofit();
        }
        return mRetrofit;
    }

    /**
     * 创建请求
     */
    public static <T> T createService(final Class<T> service) {
        return getRetrofit().create(service);
    }

    /**
     * 转换
     */
    public static <T> ObservableTransformer<BaseModel<T>, T> handleResult() {
        return new BaseModelTransformer<>();
    }


    /**
     * 下载
     */
    public static Observable<Object> download(String url) {
        return RetrofitManager.download(url, null, null);
    }

    public static Observable<Object> download(String url, String savePath, String fileName) {
        return RetrofitManager.createService(DownLoadService.class)
                .startDownLoad(url)
                .compose(RetrofitManager.handleDownload(url, savePath, fileName));
    }

    /**
     * 下载监听转换
     */
    public static ObservableTransformer<ResponseBody, Object> handleDownload(String url, String savePath, String fileName) {
        if (TextUtils.isEmpty(savePath)) {
            savePath = FileUtils.getDefaultDownLoadPath();
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = FileUtils.getDefaultDownLoadFileName(url);
        }
        return new DownLoadTransformer(savePath, fileName);
    }


    /**
     * 上传
     */
    public static Observable<Object> uploadFile(String url, List<UploadParam> params) {
//      进度Observable
        UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe();
        Observable progressObservable = Observable.create(uploadOnSubscribe);

//        组装请求
        List<MultipartBody.Part> parts = new ArrayList<>(params.size());
        for (UploadParam param : params) {
            switch (param.getType()) {
//                文本
                case UploadParam.TYPE_STRING:
                    parts.add(MultipartBody.Part.createFormData(param.getName(), param.getValue()));
                    break;
//                文件
                case UploadParam.TYPE_FILE:
                    if (param.getFile() == null || !param.getFile().exists()) {
                        break;
                    }
                    UploadRequestBody uploadRequestBody = new UploadRequestBody(param.getFile());
//                    设置总长度
                    uploadOnSubscribe.addSumLength(param.getFile().length());
                    uploadRequestBody.setUploadOnSubscribe(uploadOnSubscribe);
                    parts.add(MultipartBody.Part.createFormData(param.getName(), param.getFileName(), uploadRequestBody));
                    break;
                default:
                    break;
            }
        }

//        发起请求
        Observable uploadObservable = RetrofitManager
                .createService(UploadService.class)
                .upload(url, parts);

        return Observable.merge(progressObservable, uploadObservable);
    }

    public static Observable<Object> uploadFile(String url, UploadParam... params) {
        return RetrofitManager.uploadFile(url, Arrays.asList(params));
    }

}