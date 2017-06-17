package com.jimi_wu.easyrxretrofit;


import com.jimi_wu.easyrxretrofit.download.DownLoadService;
import com.jimi_wu.easyrxretrofit.download.DownLoadTransformer;
import com.jimi_wu.easyrxretrofit.exception.ServerException;
import com.jimi_wu.easyrxretrofit.model.BaseModel;
import com.jimi_wu.easyrxretrofit.upload.UploadOnSubscribe;
import com.jimi_wu.easyrxretrofit.upload.UploadRequestBody;
import com.jimi_wu.easyrxretrofit.utils.FileUtils;

import org.reactivestreams.Publisher;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2016/9/5.
 */
public class RetrofitUtils {

    private static RetrofitManager mRetrofitManager;

    public static void setRetrofitManager(RetrofitManager retrofitManager) {
        mRetrofitManager = retrofitManager;
    }

    /**
     * 创建请求
     */
    public static <T> T createService(final Class<T> service) {
        return mRetrofitManager.getRetrofit().create(service);
    }


    /**
     * 转换器
     * from BaseModel<T>
     * to T
     */
    public static <T> FlowableTransformer<BaseModel<T>, T> handleResult() {
        return new FlowableTransformer<BaseModel<T>, T>() {
            @Override
            public Publisher<T> apply(@NonNull Flowable<BaseModel<T>> upstream) {
                return upstream.flatMap(new Function<BaseModel<T>, Publisher<T>>() {
                    @Override
                    public Publisher<T> apply(@NonNull BaseModel<T> tBaseModel) throws Exception {
                        if (!tBaseModel.isError()) {
                            return createData(tBaseModel.getResult());
                        } else {
                            return Flowable.error(new ServerException(tBaseModel.getMsg(), tBaseModel.getErrorCode()));
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 创建Flowable<T>
     */
    public static <T> Flowable<T> createData(final T result) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(@NonNull FlowableEmitter<T> e) throws Exception {
                try {
                    e.onNext(result);
                    e.onComplete();
                } catch (Exception exception) {
                    e.onError(exception);
                }
            }
        }, BackpressureStrategy.BUFFER);
    }

    /**
     * 上传单个文件
     */
    public static <T> Flowable<Object> uploadFile(File file, Class<T> uploadServiceClass, String uploadFucntionName, Object... params) {
//      进度Observable
        UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe(file.length());
        Flowable<Integer> progressObservale = Flowable.create(uploadOnSubscribe, BackpressureStrategy.BUFFER);

        UploadRequestBody uploadRequestBody = new UploadRequestBody(file);
//      设置进度监听
        uploadRequestBody.setUploadOnSubscribe(uploadOnSubscribe);


//      创建表单主体
        MultipartBody.Part filePart =
                MultipartBody.Part.createFormData("upload", file.getName(), uploadRequestBody);


//      上传
        T service = RetrofitUtils.createService(uploadServiceClass);


        try {
//            获得上传方法的参数类型   和参数
            Class[] paramClasses = new Class[params.length + 1];
            Object[] uploadParams = new Object[params.length + 1];
            for (int i = 0; i < params.length; i++) {
                paramClasses[i] = params[i].getClass();
                uploadParams[i] = params[i];
            }
            paramClasses[params.length] = MultipartBody.Part.class;
            uploadParams[params.length] = filePart;

//            获得上传方法
            Method uploadMethod = uploadServiceClass.getMethod(uploadFucntionName, paramClasses);

//            运行上传方法
            Object o = uploadMethod.invoke(service, uploadParams);
            if (o instanceof Flowable) {
                Flowable uploadObservable = (Flowable) o;

//              合并Observable
                return Flowable.merge(progressObservale, uploadObservable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Flowable.error(e);
        }
        return Flowable.error(new ServerException("no upload method found or api service error", ServerException.ERROR_OTHER));
    }

    /**
     * 上传多个文件
     */
    public static <T> Flowable<Object> uploadFiles(ArrayList<File> files, Class<T> uploadsServiceClass, String uploadFucntionName, Object... params) {
//        总长度
        long sumLength = 0l;
        for (File file : files) {
            sumLength += file.length();
        }

//      进度Observable
        UploadOnSubscribe uploadOnSubscribe = new UploadOnSubscribe(sumLength);
        Flowable<Integer> progressObservale = Flowable.create(uploadOnSubscribe, BackpressureStrategy.BUFFER);

        ArrayList<MultipartBody.Part> fileParts = new ArrayList<>();

        for (File file : files) {

            UploadRequestBody uploadRequestBody = new UploadRequestBody(file);
//          设置进度监听
            uploadRequestBody.setUploadOnSubscribe(uploadOnSubscribe);

            fileParts.add(MultipartBody.Part.createFormData("upload", file.getName(), uploadRequestBody));
        }

//      上传
        T service = RetrofitUtils.createService(uploadsServiceClass);

        try {
//            获得上传方法的参数类型   和参数
            Class[] paramClasses = new Class[params.length + 1];
            Object[] uploadParams = new Object[params.length + 1];
            for (int i = 0; i < params.length; i++) {
                paramClasses[i] = params[i].getClass();
                uploadParams[i] = params[i];
            }
            paramClasses[params.length] = ArrayList.class;
            uploadParams[params.length] = fileParts;

//            获得上传方法
            Method uploadMethod = uploadsServiceClass.getMethod(uploadFucntionName, paramClasses);

//            运行上传方法
            Object o = uploadMethod.invoke(service, uploadParams);
            if (o instanceof Flowable) {
                Flowable uploadObservable = (Flowable) o;

//              合并Observable
                return Flowable.merge(progressObservale, uploadObservable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Flowable.error(e);
        }
        return Flowable.error(new ServerException("no upload method found or api service error", ServerException.ERROR_OTHER));
    }

    /**
     * 下载文件
     */
    public static Flowable<Object> downLoadFile(String url) {
        return downLoadFile(url, null, null);
    }

    /**
     * 下载文件
     */
    public static Flowable<Object> downLoadFile(String url, String savePath, String fileName) {

        if (savePath == null || savePath.trim().equals("")) {
            savePath = FileUtils.getDefaultDownLoadPath();
        }
        if (fileName == null || fileName.trim().equals("")) {
            fileName = FileUtils.getDefaultDownLoadFileName(url);
        }

//        下载监听
        DownLoadTransformer downLoadTransformer = new DownLoadTransformer(savePath, fileName);

        return Flowable
                .just(url)
                .flatMap(new Function<String, Publisher<ResponseBody>>() {
                    @Override
                    public Publisher<ResponseBody> apply(@NonNull String s) throws Exception {
                        DownLoadService downLoadService = RetrofitUtils.createService(DownLoadService.class);
                        return downLoadService.startDownLoad(s);
                    }
                })
                .compose(downLoadTransformer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
