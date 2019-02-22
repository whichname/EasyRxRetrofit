package com.jimi_wu.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jimi_wu.easyrxretrofit.RetrofitManager;
import com.jimi_wu.easyrxretrofit.observer.BaseObserver;
import com.jimi_wu.easyrxretrofit.observer.DownLoadObserver;
import com.jimi_wu.easyrxretrofit.observer.UploadObserver;
import com.jimi_wu.easyrxretrofit.upload.UploadParam;
import com.jimi_wu.sample.apiservice.GetUserService;
import com.jimi_wu.sample.model.FileBean;
import com.jimi_wu.sample.model.ResultBean;
import com.jimi_wu.sample.model.UserBean;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG;

    private static int REQUEST_CODE_UPLOAD = 1;
    private static int REQUEST_CODE_UPLOADS = 2;

    private TextView tv;
    private TextView tvActualProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.btn_upload).setOnClickListener(this);
        findViewById(R.id.btn_uploads).setOnClickListener(this);
        findViewById(R.id.btn_download).setOnClickListener(this);
        tv = (TextView) findViewById(R.id.tv);
        tvActualProgress = (TextView) findViewById(R.id.tv_actual_progress);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
                tvActualProgress.setText("");
                RetrofitManager
                        .createService(GetUserService.class)
                        .start()
                        .compose(RetrofitManager.<UserBean>handleResult())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseObserver<UserBean>() {
                            @Override
                            protected void _onNext(UserBean userBean) {
                                Log.i("retrofit", "onNext=========>" + userBean.getName());
                                tv.setText("请求成功:" + userBean.getName());
                            }

                            @Override
                            protected void _onError(Throwable e) {
                                Log.i("retrofit", "onError=========>" + e.getMessage());
                                tv.setText("请求失败:" + e.getMessage());
                            }
                        });
                break;
            case R.id.btn_upload:
                Intent uploadIntent = new Intent();
                uploadIntent.setType("*/*");
                uploadIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(uploadIntent, "单文件选择"), REQUEST_CODE_UPLOAD);
                break;
            case R.id.btn_uploads:
                Intent uploadsIntent = new Intent();
                uploadsIntent.setType("*/*");
                uploadsIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(uploadsIntent, "单文件选择"), REQUEST_CODE_UPLOADS);
                break;
            case R.id.btn_download:
                downLoad();
                break;
        }
    }

    private ArrayList<Uri> uris = new ArrayList<>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_UPLOAD) {
                Uri uri = data.getData();
                File file = FileUtils.getFile(this, uri);
                upload(file);
                return;
            }
            if (requestCode == REQUEST_CODE_UPLOADS) {
                uris.add(data.getData());
                if (uris.size() < 3) {
                    tv.setText("当前选择 " + uris.size() + " 个文件\n请继续选择\n3 个文件时将上传");
                    return;
                }
                ArrayList<File> files = new ArrayList<>();
                for (Uri uri : uris) {
                    File file = FileUtils.getFile(this, uri);
                    files.add(file);
                }
                uploads(files);
                uris.clear();
                return;
            }
        }
    }

    /**
     * 单图上传
     */
    public void upload(File file) {
        RetrofitManager
                .uploadFile(Constants.UPLOAD_URL, new UploadParam("upload", file))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UploadObserver<ResultBean<FileBean>>() {

                    @Override
                    public void _onNext(ResultBean<FileBean> fileBeanResultBean) {
                        Log.i("retrofit", "onNext=======>url:" + fileBeanResultBean.getData().getUrl());
                        tv.setText("上传成功:" + fileBeanResultBean.getData().getUrl());
                    }

                    @Override
                    public void _onProgress(Integer percent) {
                        Log.i("retrofit", "onProgress======>" + percent);
                        tv.setText("上传中:" + percent);
                    }

                    @Override
                    public void _onProgress(long uploaded, long sumLength) {
                        tvActualProgress.setText("上传中:" + uploaded + "/" + sumLength);
                    }

                    @Override
                    public void _onError(Throwable e) {
                        Log.i("retrofit", "onError======>" + e.getMessage());
                        tv.setText("上传失败:" + e.getMessage());
                    }
                });
    }


    /**
     * 多图上传
     */
    public void uploads(ArrayList<File> files) {
        ArrayList<UploadParam> uploadParams = new ArrayList<>(files.size());
        for (File file : files) {
            uploadParams.add(new UploadParam("upload", file));
        }
        RetrofitManager
                .uploadFile(Constants.UPLOADS_URL,
                        uploadParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UploadObserver<ResultBean<ArrayList<FileBean>>>() {

                    @Override
                    public void _onNext(ResultBean<ArrayList<FileBean>> arrayListResultBean) {
                        ArrayList<FileBean> fileBeans = arrayListResultBean.getData();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (FileBean fileBean : fileBeans) {
                            stringBuilder.append("上传成功:" + fileBean.getUrl() + "\n");
                        }
                        Log.d("retrofit", "onNext=======>" + stringBuilder);
                        tv.setText("上传成功:" + stringBuilder);
                    }

                    @Override
                    public void _onProgress(Integer percent) {
                        Log.i("retrofit", "onProgress=======>" + percent);
                        tv.setText("上传中:" + percent);
                    }

                    @Override
                    public void _onProgress(long uploaded, long sumLength) {
                        tvActualProgress.setText("上传中:" + uploaded + "/" + sumLength);
                    }

                    @Override
                    public void _onError(Throwable e) {
                        Log.d("retrofit", "onError======>" + e.getMessage());
                        tv.setText("上传失败:" + e.getMessage());
                    }
                });
    }

    /**
     * 文件下载
     */
    public void downLoad() {
        RetrofitManager
                .download("/uploads/test.pdf")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DownLoadObserver() {
                    @Override
                    public void _onNext(String result) {
                        Log.i("retrofit", "onNext=======>" + result);
                        tv.setText("下载成功:" + result);
                    }

                    @Override
                    public void _onProgress(Integer percent) {
                        Log.i("retrofit", "onProgress=======>" + percent);
                        tv.setText("下载中:" + percent);
                    }

                    @Override
                    public void _onProgress(long uploaded, long sumLength) {
                        tvActualProgress.setText("下载中:" + uploaded + "/" + sumLength);
                    }

                    @Override
                    public void _onError(Throwable e) {
                        Log.i("retrofit", "onProgress=======>" + e.getMessage());
                        tv.setText("下载失败:" + e.getMessage());
                    }
                });
    }


}
